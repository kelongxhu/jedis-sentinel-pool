package redis.client.util;


import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.pool2.impl.BaseObjectPoolConfig;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import redis.clients.jedis.Cluster;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class XMLParse {
    private final static Logger LOG = Logger.getLogger(XMLParse.class);

    private SAXBuilder sax;

    private String lastFile = "redis-cluster.xml";

    public XMLParse() {
        sax = new SAXBuilder();
    }

    public Map<String, Cluster> load(String filename) {
        String loadfile;
        Path path = Paths.get(FileUtil.getPath(lastFile));
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            loadfile = lastFile;
        } else {
            loadfile = filename;
        }
        return this.parse(loadfile);
    }

    public Map<String, Cluster> parse(String filename) {
        Map<String, Cluster> clusterMap = new HashMap<String, Cluster>();
        try {
            Document doc = sax.build(FileUtil.getConfigFile(filename));
            // root element
            Element root = doc.getRootElement();
            if ("clusters".equalsIgnoreCase(root.getName())) {
                Cluster cluster;
                // get cluster elements
                for (Element e : root.getChildren("cluster")) {
                    cluster = new Cluster();
                    String name = e.getAttributeValue("name");
                    cluster.setName(name);
                    // get masters
                    List<String> masters = new ArrayList<>();
                    Set<String> sentinels = new HashSet<String>();

                    Element mastersE = e.getChild("masters");
                    for (Element masterE : mastersE.getChildren("master")) {
                        masters.add(masterE.getValue());
                    }
                    //get sentinels
                    Element sentinelsE = e.getChild("sentinels");
                    for (Element sentinelE : sentinelsE.getChildren("sentinel")) {
                        sentinels.add(sentinelE.getValue());
                    }
                    // get pool setting
                    Element poolE = e.getChild("pool-config");
                    if (poolE != null) {
                        JedisPoolConfig cfg = new JedisPoolConfig();
                        cluster.setConfig(cfg);
                        for (Element pInfo : poolE.getChildren()) {
                            autoConfig(cfg, pInfo.getName(), pInfo.getValue(),
                                    fields(GenericObjectPoolConfig.class, BaseObjectPoolConfig.class));
                        }
                    }
                    //check right
                    if (masters.size() == 0 || sentinels.size() == 0) {
                        continue;
                    }
                    cluster.setMasters(masters);
                    cluster.setSentinels(sentinels);
                    clusterMap.put(name, cluster);
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return clusterMap;
    }

    /**
     * @param instance
     * @param property
     * @param value
     * @param fields
     */
    private void autoConfig(Object instance, String property, Object value, Field[] fields) {
        String name;
        for (Field field : fields) {
            name = field.getName();
            if (name.equals(property)) {
                try {
                    BeanUtils.setProperty(instance, name, value);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * @param clazzs
     * @return
     */
    @SuppressWarnings("all")
    private static Field[] fields(Class... clazzs) {
        int length = 0;
        Field[][] all = new Field[clazzs.length][];
        for (int i = 0; i < clazzs.length; i++) {
            all[i] = clazzs[i].getDeclaredFields();
            length += all[i].length;
        }
        Field[] fields = new Field[length];
        int offset = 0;
        for (Field[] f : all) {
            System.arraycopy(f, 0, fields, offset, f.length);
            offset += f.length;
        }
        return fields;
    }
}