package net.heroicefforts.gswitchblade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;

/**
 * This class starts a daemon thread that reads Win32 messages submitted to the app by the
 * JNA DLL.
 * 
 * @author jevans
 *
 */
public class MessagePump {
	private static final Logger log = LoggerFactory.getLogger(MessagePump.class);
			
	private Thread pumpThread;
	

	public MessagePump() {
		pumpThread = new Thread(new Runnable() {
			@Override
			public void run() {
				init();
				processMessages();
			}
		});
		pumpThread.setDaemon(true);
		pumpThread.start();
	}
	
	public void init() {
		//no-op
	}
	
	public void processMessages() {
		log.debug("Pump created.");
		WinUser.MSG msg = new WinUser.MSG();
		while(true) {
			while (User32.INSTANCE.PeekMessage(msg, null, 0, 0, 1)) {
				User32.INSTANCE.TranslateMessage(msg);
				User32.INSTANCE.DispatchMessage(msg);
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				break;
			}
		}
		log.debug("Pump shut down.");
	}

}
