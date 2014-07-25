package net.heroicefforts.switchblade

import javax.swing.SwingUtilities

import com.sun.jna.WString
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinUser
import net.heroicefforts.switchblade.SwitchBladeSDK2Library.DynamicKeyState

println 'Hello'


def keyStateCallback = new DynamicKeyState() {
		public int invoke(int key, int keyState) {
			println "Key state change $key:$keyState"
			return 0
		}
	}

SwitchBladeSDK2Library sdk = null
try {
	SwitchBladeMessagePump pump = new SwitchBladeMessagePump();
	sdk = SwitchBladeSDK2Library.INSTANCE;
	def res = sdk.RzSBStart()
	println res
	res = sdk.RzSBSetImageDynamicKey(1, 1, new WString("C:/work/dev/java/workspace/switchblade-component/resources/snowman.png"))
	res = sdk.RzSBSetImageDynamicKey(1, 2, new WString("C:/work/dev/java/workspace/switchblade-component/resources/christmas_pudding.png"))
	res = sdk.RzSBDynamicKeySetCallback(keyStateCallback)
	println res
	println Integer.toHexString(res)
	Thread.sleep(10000)
}
catch(Throwable t) {
	t.printStackTrace()
}
finally {
	if(sdk != null) {
		sdk.RzSBStop()
	}
}
