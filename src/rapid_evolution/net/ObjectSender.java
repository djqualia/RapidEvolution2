package rapid_evolution.net;

import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

public class ObjectSender extends Thread {

    private static Logger log = Logger.getLogger(MyConnectQueryThread.class);
	
	private Object result;
	
	public ObjectSender(Object result) {
		this.result = result;
	}
	
	public void run() {
		try {
		  if (log.isTraceEnabled())
			  log.trace("run(): sending data mining result=" + result);
		  Socket sock = new Socket("mixshare.dyndns.org", 4205);
		  OutputStream os = sock.getOutputStream();
		  ObjectOutput out = new ObjectOutputStream(os);
		  out.writeObject(result);
		  out.close();
		  os.close();
		  sock.close();		  
		} catch (Exception e) {
			log.error("run(): error", e);
		}
	}
	
}
