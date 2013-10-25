package rapid_evolution.threads;

import rapid_evolution.ui.SkinManager;

import com.ibm.iwt.IOptionPane;

public class ErrorMessageThread extends Thread {
    private static int num_threads = 0;
  public ErrorMessageThread(String errortitle, String errortext)  { this.errortitle = errortitle; this.errortext = errortext; }
  private String errortitle;
  private String errortext;
  public void run()  {
      if (num_threads == 0) {
          num_threads++;          
          IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"),
                  errortext,
                errortitle,
                IOptionPane.ERROR_MESSAGE);
          num_threads--;
      }
  }
}
