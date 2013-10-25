package rapid_evolution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import rapid_evolution.filefilters.FileFormats;
import rapid_evolution.ui.OptionsUI;

public class FileUtil {
    private FileUtil() { }

    public static void RecurseFileTree(String directory, Vector element) {
      File dir = new File(directory);
      if ((dir != null) && (dir.listFiles() != null)) {
      File files[] = new File[dir.listFiles().length];
      files = dir.listFiles();
      files = sortfiles(files);
      for (int i = 0; i < files.length; ++i) {
        if (!files[i].isDirectory()) {
            
            if (FileFormats.acceptsFile(files[i].getName())) {
              element.add(files[i].getAbsolutePath());
          }
        } else {
          RecurseFileTree(files[i].getAbsolutePath(), element);
        }
      }
      }
    }

    public static void RecurseFileTree2(File dir, Vector element) {
      if (dir != null) {
        if (dir.listFiles() != null) {
        File files[] = new File[dir.listFiles().length];
        files = dir.listFiles();
        files = FileUtil.sortfiles(files);
        for (int i = 0; i < files.length; ++i) {
          if (!files[i].isDirectory()) element.add(files[i]);
          else {
            RecurseFileTree2(files[i], element);
          }
        }
        }
      }
    }

    public static File[] sortfiles(File[] files) {
    	String[] strArray = new String[files.length];
    	for (int s = 0; s < strArray.length; ++s)
    		strArray[s] = files[s].getAbsolutePath();    	
    	java.util.Arrays.sort(strArray, String.CASE_INSENSITIVE_ORDER);
    	for (int s = 0; s < strArray.length; ++s)
    		files[s] = new File(strArray[s]);
    	return files;
    }

    public static String getExtension(File f) {
      return getExtension(f.getName());
    }
    
    public static String getExtension(String s) {
        String ext = null;
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
          ext = s.substring(i+1).toLowerCase();
        }
        return ext;
      }    
    
    static public boolean fileAlreadyExists(String filename) {
        boolean returnval = false;
        File file = new File(filename);
        if (file.exists()) returnval = true;
        file = null;
        return returnval;
    }
    
    public static String getDirectoryFromFilename(String filename) {
        if ((filename == null) || filename.equals("")) return null;
        File efile = getFileObject(filename);
        String name = efile.getName();
        String path = efile.getPath();
        int pos = path.indexOf(name);
        efile = new File(path.substring(0, pos));
        //if (efile.isDirectory()) {
          return path.substring(0, pos);          
        //}        
        //return null;
    }

    public static String getFilenameMinusDirectory(String filename) {
        if ((filename == null) || filename.equals(""))
        	return null;
        File efile = getFileObject(filename);
        String name = efile.getName();
        return name;
    }
    
    static public File getFileObject(String filename) {
        if (filename == null) return new File(filename);
        if (filename.startsWith("/")) return new File(filename);
        if ((filename.length() > 1) && filename.charAt(1) == ':') {
            File file = new File(filename);
            if (!OptionsUI.instance.portablemusicmode.isSelected()) return file;
            else {
                if (file.exists() && file.isFile()) return file;
                if (filename.length() > 3) {
                    for (char drive = 'c'; drive < 'z'; ++drive) {
                        String newfilename = drive + filename.substring(1);
                        file = new File(newfilename);
                        if (file.exists() && file.isFile()) return file;                    
                    }
                }
            }
        }
        return new File("/" + filename);
        
        /*
         * apparently this was causing memory leaks?
        File file = new File(filename);
        if (file.exists() && file.isFile()) return file;
        file = new File("/" + filename);        
        if (file.exists() && file.isFile()) return file;
        return new File(filename);
        */
    }    
    
    public static void copy(String fromFileName, String toFileName)
            throws IOException {
        File fromFile = new File(fromFileName);
        File toFile = new File(toFileName);

        if (!fromFile.exists())
            throw new IOException("FileCopy: " + "no such source file: "
                    + fromFileName);
        if (!fromFile.isFile())
            throw new IOException("FileCopy: " + "can't copy directory: "
                    + fromFileName);
        if (!fromFile.canRead())
            throw new IOException("FileCopy: " + "source file is unreadable: "
                    + fromFileName);

        if (toFile.isDirectory())
            toFile = new File(toFile, fromFile.getName());

        if (toFile.exists()) {
            if (!toFile.canWrite())
                throw new IOException("FileCopy: "
                        + "destination file is unwriteable: " + toFileName);
//            throw new IOException("FileCopy: "
//                      + "existing file will not be overwritten.");
        } else {
            String parent = toFile.getParent();
            if (parent == null)
                parent = System.getProperty("user.dir");
            File dir = new File(parent);
            if (!dir.exists())
                throw new IOException("FileCopy: "
                        + "destination directory doesn't exist: " + parent);
            if (dir.isFile())
                throw new IOException("FileCopy: "
                        + "destination is not a directory: " + parent);
            if (!dir.canWrite())
                throw new IOException("FileCopy: "
                        + "destination directory is unwriteable: " + parent);
        }

        FileInputStream from = null;
        FileOutputStream to = null;
        try {
            from = new FileInputStream(fromFile);
            to = new FileOutputStream(toFile);
            byte[] buffer = new byte[32768]; //[4096];
            int bytesRead;

            while ((bytesRead = from.read(buffer)) != -1)
                to.write(buffer, 0, bytesRead); // write
        } finally {
            if (from != null)
                try {
                    from.close();
                } catch (IOException e) {
                    ;
                }
            if (to != null)
                try {
                    to.close();
                } catch (IOException e) {
                    ;
                }
        }
    }

    static public String getFileSeperator() {
        return System.getProperty("file.separator");
    }
    
    static public String correctPathSeperators(String filename) {
        String seperator = getFileSeperator();
        if (seperator.equals("/"))
            return StringUtil.ReplaceString("\\", filename, "/");        
        else if (seperator.equals("\\"))
            return StringUtil.ReplaceString("/", filename, "\\");
        return filename;
    }
    
}
