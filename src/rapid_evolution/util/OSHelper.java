package rapid_evolution.util;

import java.io.File;

public class OSHelper {

	static public final int UNKNOWN = 0;
	static public final int LINUX = 1;
	static public final int SOLARIS = 2;
	static public final int WINDOWS = 3;
	static public final int MACOS = 4;
	
	static public boolean isWindows() {
		return (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0);
	}
	
	static public File getFileBackwardsCompatible(String filename) {
		File file = new File(getWorkingDirectory() + "/" + filename);
		if (file.exists())
			return file;
		return new File(filename);
	}
	
    public static File getWorkingDirectory() {
    	final String applicationName = "Rapid Evolution 2";
        final String userHome = System.getProperty("user.home", ".");
        final File workingDirectory;
        switch (getPlatform()) {
            case LINUX:
            case SOLARIS:
                workingDirectory = new File(userHome, applicationName + '/');
                break;
            case WINDOWS:
                final String applicationData = System.getenv("APPDATA");
                if (applicationData != null)
                    workingDirectory = new File(applicationData, applicationName + '/');
                else
                    workingDirectory = new File(userHome, applicationName + '/');
                break;
            case MACOS:
                workingDirectory = new File(userHome, "Library/Application Support/" + applicationName);
                break;
            default:
                return new File(".");
        }
        if (!workingDirectory.exists())
            if (!workingDirectory.mkdirs())
                throw new RuntimeException("The working directory could not be created: " + workingDirectory);
        return workingDirectory;
    }    
    
    public static int getPlatform() {    
	    final String sysName = System.getProperty("os.name").toLowerCase();
	    if (sysName.contains("windows"))
	        return WINDOWS;
	    else if (sysName.contains("mac"))
	        return MACOS;
	    else if (sysName.contains("linux"))
	        return LINUX;
	    else if (sysName.contains("solaris"))
	        return SOLARIS;
	    else
	    	return UNKNOWN;
    }
    
    static public void main(String[] args) {
    	System.out.println(getWorkingDirectory());
    }
    
	
}
