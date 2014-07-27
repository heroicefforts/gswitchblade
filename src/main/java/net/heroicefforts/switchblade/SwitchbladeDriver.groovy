package net.heroicefforts.switchblade

import com.sun.jna.WString
import net.heroicefforts.switchblade.SwitchBladeSDK2Library.DynamicKeyState
import net.heroicefforts.switchblade.SwitchBladeSDK2Library.GestureCallback
import net.heroicefforts.switchblade.SwitchbladeState.KeyState

class SwitchbladeDriver {
	private SwitchBladeSDK2Library sdk
	private GestureCallback gestureCallback
	private DynamicKeyState keyStateCallback
	
	private SwitchbladeState currentState
	

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
	}
	
	void onClick(int key) {
		currentState?.states[key]?.onClick()
	}
	
	public void setState(SwitchbladeState state) {
		this.currentState = state
		println this.currentState
		println this.currentState.states
		this.currentState.states.each { k, KeyState v ->
			sdk.RzSBSetImageDynamicKey(k, 1, new WString(v.upImagePath))
		}
	}
	
	public void start(SwitchbladeState state) {
		SwitchBladeMessagePump pump = new SwitchBladeMessagePump()
		sdk = SwitchBladeSDK2Library.INSTANCE;
		//TODO add exception handling
		def res = sdk.RzSBStart()
//		if(appEventCallback)
//			sdk.RzSBAppEventSetCallback(appEventCallback)
		if(keyStateCallback)
			sdk.RzSBDynamicKeySetCallback(keyStateCallback)
		if(gestureCallback)
			sdk.RzSBGestureSetCallback(gestureCallback)
		
		if(state)
			setState(state)
	}
	
	public void shutdown() {
		if(sdk)
			sdk.RzSBStop()
	}

	protected static class DefaultDynamicKeyCallback implements DynamicKeyState {
		private SwitchbladeDriver driver
		public int invoke(int key, int keyState) {
			println "Key state change $key:$keyState"
			//TODO enum
			if(keyState == 2)
				driver.onClick(key)
			return 0
		}
	}
	
	
	static {
		SwitchbladeDriver.metaClass.methodMissing = {String name, args ->
			def keyImgMatcher = (name =~ /setKey([1-9]|10)(Up|Down)Image/)
			if(keyImgMatcher.matches()) {
				delegate.metaClass."$name" = {String imagePath -> sdk.RzSBSetImageDynamicKey(keyImgMatcher[0][1] as Integer, keyImgMatcher[0][2] == "Up" ? 1 : 2, new WString(imagePath)) }
				delegate.invokeMethod("$name", args[0])
			}
			else throw new MissingMethodException(name, delegate, args)
		}
	}

}



