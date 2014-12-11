package redis.client.util;


public class StringUtil {
	
	public static String append(Object...args) {
		StringBuffer buf = new StringBuffer();
		if(args != null && args.length > 0) {
			for (Object obj : args) {
				buf.append(obj);
			}
		}
		return buf.toString();
	}
}