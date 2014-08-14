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

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.WString;
import com.sun.jna.win32.StdCallLibrary;

/**
 * JNA interface to the Switchblade SDK DLL.
 * 
 * @author jevans
 *
 */
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
	
	int RzSBRenderBuffer(int target, BufferParams.ByReference bufferParams);
	
	public static final int INVALID_ARG = 0x80070057;
	
}