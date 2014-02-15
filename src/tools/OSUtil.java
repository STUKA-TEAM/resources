package tools;

/**
 * @Title: OSUtil
 * @Description: Define a class to detect the type of current OS 
 * @Company: ZhongHe
 * @author ben
 * @date 2013年11月10日
 */
public class OSUtil {
	private final static String OS = System.getProperty("os.name").toLowerCase();
	
	public final static boolean isWindows() {		 
		return (OS.indexOf("win") >= 0);
	}
 
	public final static boolean isMac() { 
		return (OS.indexOf("mac") >= 0);
	}
 
	public final static boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
	}
 
	public final static boolean isSolaris() {
		return (OS.indexOf("sunos") >= 0);
	}
	
}
