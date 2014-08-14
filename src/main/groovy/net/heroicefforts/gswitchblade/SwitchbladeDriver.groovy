/*
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
package net.heroicefforts.gswitchblade

import com.sun.jna.Memory
import com.sun.jna.Pointer
import com.sun.jna.WString
import com.sun.jna.platform.win32.WinDef
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import java.awt.AlphaComposite
import java.awt.Graphics2D
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.awt.image.Raster
import java.awt.RenderingHints
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import net.heroicefforts.gswitchblade.SwitchBladeSDK2Library.DynamicKeyState
import net.heroicefforts.gswitchblade.SwitchBladeSDK2Library.GestureCallback
import net.heroicefforts.gswitchblade.SwitchbladeState.KeyState

/**
 * Groovy wrapper around the Switchblade JNA interface.<br/>
 * <br/>
 * The following dynamic methods are provided in addition to the static:<br/>
 * <blockquote>
 * <span style="color:limegreen;">setKey<i>N</i>UpImage(String pathToCorrectlySizedImageFile)</span>, where N is 1 to ...<br/>
 * <span style="color:limegreen;">setKey<i>N</i>DownImage(String pathToCorrectlySizedImageFile)</span>, where N is 1 to ...<br/>
 * <br/>
 * <span style="color:limegreen;text-decoration: line-through;">renderToKey<i>N</i>(BufferedImage)</span>, where N is 1 to ... (Not presently working -- Razer bug)<br/>
 * <span style="color:limegreen;text-decoration: line-through;">renderToKey<i>N</i>(Closure g2dwork)</span>, where N is 1 to ... (Not presently working -- Razer bug)<br/>
 * <br/>
 * <span style="color:limegreen;">renderToTouchPad(BufferedImage)</span><br/>
 * <span style="color:limegreen;">renderToTouchPad(Closure g2dwork)</span>, where g2dwork is a drawing closure of form:<br/>
 * 		Closure(Graphics2d d, int width, int height)<br/>
 * </blockquote>
 * 
 * @author jevans
 *
 */
@Slf4j
class SwitchbladeDriver {
	private static final Lock sbLock = new ReentrantLock();
	private static final long LONG_PRESS_MILLIS = 2000
	
	private SwitchBladeSDK2Library sdk
	private GestureCallback gestureCallback
	private DynamicKeyState keyStateCallback
	private SwitchBladeMessagePump pump
	private SwitchbladeState currentState
	private boolean started = false;

	private Map<String, RenderElement> renderElements
	private Map<Integer, Long> keyDownTime = [:]
	
	private File blankIcon

	
	SwitchbladeDriver() {
		this(new DefaultDynamicKeyCallback(), null) 
	}
		
	SwitchbladeDriver(GestureCallback gestureCallback) {
		this(new DefaultDynamicKeyCallback(), gestureCallback) 
	}
	
	protected SwitchbladeDriver(DynamicKeyState keyStateCallback, GestureCallback gestureCallback) {
		this.keyStateCallback = keyStateCallback
		this.gestureCallback = gestureCallback
		this.keyStateCallback.driver = this
		
		unpackResources()
		initRenderers() //TODO source from Capabilities API
	}

	/**
	 * Sets the key and widget states for the Switchblade.
	 * 	
	 * @param state
	 */
	public void setState(SwitchbladeState state) {
		sbLock.lock();
		try {
			this.currentState = state
			this.currentState.states.each { k, KeyState v ->
				log.debug "SetImage $k - $v.upImagePath"
				def res = sdk.RzSBSetImageDynamicKey(k, 1, new WString(v.upImagePath))
			}
			def undefinedKeys = new HashSet(1..10)
			undefinedKeys.removeAll(this.currentState.states.keySet())
			undefinedKeys.each {
				log.debug "SetImage $it blank"
				sdk.RzSBSetImageDynamicKey(it, 1, new WString(blankIcon.absolutePath))
			}			
		}
		finally {
			sbLock.unlock();
		}
	}
	
	/**
	 * Connects to the Switchblade and sets an initial state.
	 * 
	 * @param state
	 */
	public void start(SwitchbladeState state) {
		sbLock.lock();
		try {
			if(!started) {
				log.debug "Starting Switchblade"
				if(!pump) {
					pump = new SwitchBladeMessagePump()
					sdk = SwitchBladeSDK2Library.INSTANCE;
				}
				
				//TODO add exception handling
				def res = sdk.RzSBStart()
				
		//		if(appEventCallback)
		//			sdk.RzSBAppEventSetCallback(appEventCallback)
				if(keyStateCallback)
					sdk.RzSBDynamicKeySetCallback(keyStateCallback)
					
				if(gestureCallback)
					sdk.RzSBGestureSetCallback(gestureCallback)
				
				this.started = true;
			}
	
			if(state)
				setState(state)
		}
		finally {
			sbLock.unlock();
		}
	}
	
	/**
	 * Shuts down the apps connection to the Switchblade.
	 */
	public void shutdown() {
		sbLock.lock();
		try {
			if(started) {
				def res = sdk.RzSBStop()
				this.started = false;
				log.debug "Shutdown:  $res"
			}
		}
		finally {
			sbLock.unlock()
		}
	}

	//TODO delete me
	@PackageScope
	void stop() {
		sdk.RzSBStop()
	}

	/**
	 * Try with resource convenience method for the driver.
	 * @param state
	 * @param c
	 */
	public void with(SwitchbladeState state, Closure c) {
		try {
			this.start(state)
			c()
		}
		finally {
			this.shutdown()
		}
	}

	protected static class DefaultDynamicKeyCallback implements DynamicKeyState {
		private SwitchbladeDriver driver
		public int invoke(int key, int keyState) {
			log.debug "Key state change $key:$keyState"
			//TODO enum
			if(keyState == 2)
				driver.keyDownTime.put(key, System.currentTimeMillis())
			else if(keyState == 1) {
				long now = System.currentTimeMillis()
				Long down = driver.keyDownTime.get(key)
				if(down != null && (now - down > LONG_PRESS_MILLIS))
					driver.onLongPress(key)
				else
					driver.onClick(key)
			}
			return 0
		}
	}
	
	/**
	 * Renders the given image to the specified Switchblade LCD.
	 * 
	 * @param elementName
	 * @param image
	 */
	protected void render(String elementName, BufferedImage image) {
		render(elementName, { Graphics2D g, int w, int h ->
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(image, 0, 0, w, h, null)
		});
	}

	/**
	 * Allows the supplied closure to draw directly to the specified Switchblade LCD.  
	 * Takes a closure of the form:<br/>
	 * Closure(Graphics2D, int width, int height)
	 * 
	 * @param elementName the LCD name
	 * @param graphicsWork
	 */
	protected void render(String elementName, Closure graphicsWork) {
		def renderer = renderElements[elementName]
		if(!renderer)
			throw new IllegalArgumentException("No such resource '$elementName'.  Available resources:  ${renderElements.keySet().join(', ')}.")
		renderer.draw(graphicsWork).commit()
		sdk.RzSBRenderBuffer(renderer.id, renderer.bufferParams)
	}

	private void initRenderers() {
		renderElements = [TouchPad:new RenderElement((1 << 16 | 0), 800, 480)]
		(1..10).each {
			renderElements["Key$it"] = new RenderElement((1 << 16 | it), 115, 115)
		}
	}

	private void onClick(int key) {
		if(currentState?.states[key]?.onClick)
			currentState.states[key].onClick()
	}
	
	private void onLongPress(int key) {
		def keyState = currentState?.states[key]
		if(keyState) {
			if(keyState.onLongPress)
				keyState.onLongPress()
			else if(keyState.onClick)
				keyState.onClick()
		}
	}
	
	private void unpackResources() {
		Thread.currentThread().contextClassLoader.getResourceAsStream('icons/blank.png').withStream {
			blankIcon = File.createTempFile('gswitchblade_blank_icon', '.png')
			blankIcon.deleteOnExit()
			blankIcon.withOutputStream { os ->
				os << it
			}
		}
	}

	static {	
		SwitchbladeDriver.metaClass.methodMissing = {String name, args ->
			def keyImgMatcher = (name =~ /setKey([1-9]|10)(Up|Down)Image/)
			if(keyImgMatcher.matches()) {
				delegate.metaClass."$name" = { String imagePath -> 
					sdk.RzSBSetImageDynamicKey(keyImgMatcher[0][1] as Integer, 
						keyImgMatcher[0][2] == "Up" ? 1 : 2, new WString(imagePath)) 
				}
				delegate.invokeMethod("$name", args[0])
				return
			}
			
			def renderMatcher = (name =~ /renderTo(TouchPad|Key(?:[1-9]|10))/)
			if(renderMatcher.matches() && args.size() == 1 && (args[0] instanceof BufferedImage || args[0] instanceof Closure)) {
				delegate.metaClass."$name" = { arg1 -> 
					delegate.render(renderMatcher[0][1], arg1) 
				}
				delegate.invokeMethod("$name", args[0])
				return 
			}
			
			throw new MissingMethodException(name, delegate.class, args)
		}
	}

}
