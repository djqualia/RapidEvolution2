package rapid_evolution.threads;

import org.apache.log4j.Logger;

import rapid_evolution.ui.SkinManager;

import com.ibm.iwt.IOptionPane;

public class OutOfMemoryThread extends Thread {
    private static Logger log = Logger.getLogger(OutOfMemoryThread.class);

    private static int num_threads = 0;
  public OutOfMemoryThread()  { }
  public void run()  {
      log.error("run(): out of memory, free memory: " + Runtime.getRuntime().freeMemory() / 1048576 + "mb");
                  
      if (num_threads == 0) {
          num_threads++;          
          IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"),
                  SkinManager.instance.getDialogMessageText("out_of_memory_error"),
                SkinManager.instance.getDialogMessageTitle("out_of_memory_error"),
                IOptionPane.ERROR_MESSAGE);
          num_threads--;
      }
      
      Runtime.getRuntime().gc();
      
  }
}
