package net.heroicefforts.switchblade;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;

public class MessagePump {
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
		System.out.println("Pump created.");
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
	}

}
