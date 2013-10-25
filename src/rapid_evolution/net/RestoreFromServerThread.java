package rapid_evolution.net;

import rapid_evolution.RapidEvolution;
import rapid_evolution.net.MixshareClient;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class RestoreFromServerThread extends Thread {
  public void run() {
    MixshareClient.instance.servercommand.restoreFromServer();
  }
}
