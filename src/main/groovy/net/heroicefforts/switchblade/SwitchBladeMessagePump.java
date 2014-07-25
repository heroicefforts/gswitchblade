package net.heroicefforts.switchblade;

public class SwitchBladeMessagePump extends MessagePump {
	public void init() {
		//Switchblade messages are dispatched to the first thread to load the library.
		@SuppressWarnings("unused")
		SwitchBladeSDK2Library sdk = SwitchBladeSDK2Library.INSTANCE;
	}

}
