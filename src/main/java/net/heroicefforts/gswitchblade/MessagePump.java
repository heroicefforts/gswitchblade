/**
 * Copyright 2014 Heroic Efforts LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Disclaimer: gswitchblade is in no way affiliated
 * with Razer and/or any of its employees and/or licensors.
 * Heroic Efforts LLC does not take responsibility for any harm caused, direct
 * or indirect, to any Razer peripherals via the use of gswitchblade.
 *
 * "Razer" is a trademark of Razer USA Ltd.
 */
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
