package utils;

public class StringUtils {
	public static String getNameFromPath(String path) {
		String separator = System.getProperty("file.separator");
		int lastSeparatorIndex = path.lastIndexOf(separator);
		
		return path.substring(lastSeparatorIndex + 1);
	}
}
