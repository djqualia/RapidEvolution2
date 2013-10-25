package rapid_evolution.threads;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import net.sf.jftp.net.FtpClient;

import org.apache.log4j.Logger;

import rapid_evolution.RapidEvolution;
import rapid_evolution.ui.SkinManager;

import com.ibm.iwt.IOptionPane;

public class AutoUpdateThread extends Thread {

    private static Logger log = Logger.getLogger(AutoUpdateThread.class);
    
    public static boolean keepwaitingtoupgrade = true;
  public AutoUpdateThread() { }
  public void run() {
      if (1 > 0) return; // disable this for now
    try {
      File updfile = new File("re2update.txt");
      updfile.delete();
    } catch (Exception e) { }
    try {
        FtpClient ftpclient = new FtpClient();
        ftpclient.setUsername("re2user@mixshare.com");
        ftpclient.setPassword("re2user");
        ftpclient.login("www.mixshare.com");
        ftpclient.get("re2update.txt");
        while ((!timetoupgrade() && keepwaitingtoupgrade) && !RapidEvolution.instance.terminatesignal) {
          Thread.sleep(10000);
        }
        if (keepwaitingtoupgrade) {
            log.error("run(): application upgrade is needed");
          if (!RapidEvolution.instance.terminatesignal) {
            ftpclient.get("re2update.jar");
            while (!updatedappversion && !RapidEvolution.instance.terminatesignal) {
              Thread.sleep(15000);
              UpdateVersion(true);
            }
          }
        } else log.debug("run(): no application upgrade is needed");

    } catch (Exception e) { }
  }

  boolean timetoupgrade() {
    try {
      FileReader inputstream = new FileReader("re2update.txt");
      BufferedReader inputbuffer = new BufferedReader(inputstream);
      String filesizestr = inputbuffer.readLine();
      inputbuffer.close();
      inputstream.close();
      if (!filesizestr.equals("")) {
        File check = new File("rapid_evolution.jar");
        String str1 = String.valueOf(check.length());
        String str2 = filesizestr;
        if (!str1.equals(str2)) return true;
        else keepwaitingtoupgrade = false;
      }
    } catch (Exception e) { }
    return false;
  }

  static boolean timetoupgrade = false;
  static boolean updatedappversion = false;
  static public void UpdateVersion(boolean displaynotification) {
      if (updatedappversion) return;
      try {
        FileReader inputstream = new FileReader("re2update.txt");
        BufferedReader inputbuffer = new BufferedReader(inputstream);
        String filesizestr = inputbuffer.readLine();
        inputbuffer.close();
        inputstream.close();
        File update = new File("re2update.jar");
        if (!filesizestr.equals("") && filesizestr.equals(String.valueOf(update.length()))) {
            File oldapp = new File("rapid_evolution.jar");
            if (oldapp.length() != update.length()) {
              timetoupgrade = true;
              String[] command = new String[3];
              command[0] = "java";
              command[1] = "-jar";
              command[2] = "upgrade.jar";
              Process p = Runtime.getRuntime().exec(command);
              if (displaynotification) {
                  IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"),
                     SkinManager.instance.getDialogMessageText("rapid_evolution_upgrade"),
                    SkinManager.instance.getDialogMessageTitle("rapid_evolution_upgrade"),
                    IOptionPane.INFORMATION_MESSAGE);
              }
            }
            oldapp = new File("re2update.txt");
            oldapp.delete();
            updatedappversion = true;
        }
      } catch (Exception e) { }
  }

}
