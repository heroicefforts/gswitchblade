package net.heroicefforts.switchblade;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.WString;
import com.sun.jna.win32.StdCallLibrary;

public interface SwitchBladeSDK2Library extends StdCallLibrary {
	public static final String JNA_LIBRARY_NAME = "RzSwitchBladeSDK2";
	public static final NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(SwitchBladeSDK2Library.JNA_LIBRARY_NAME);
	public static final SwitchBladeSDK2Library INSTANCE = (SwitchBladeSDK2Library)Native.loadLibrary(SwitchBladeSDK2Library.JNA_LIBRARY_NAME, SwitchBladeSDK2Library.class);

	int RzSBStart();
	int RzSBStop();

	int RzSBSetImageDynamicKey(int key, int keyState, WString iconPath);
	int RzSBSetImageTouchpad(WString iconPath);
	
	int RzSBDynamicKeySetCallback(DynamicKeyState state);
	
	int RzSBAppEventSetCallback(AppEventCallback cb);
	
	interface DynamicKeyState extends StdCallCallback {
        int invoke(int key, int keyState);
    }
	
	interface AppEventCallback extends StdCallCallback {
		int invoke(int appEvent, int wparam, int lparam);
	}
	
	int RzSBGestureSetCallback(GestureCallback gb);
	
	interface GestureCallback extends StdCallCallback {
		int invoke(int gesture, int dwParams, short x, short y, short z);
	}
	
	public static final int INVALID_ARG = 0x80070057;
	
}