package net.heroicefforts.switchblade

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import net.heroicefforts.switchblade.SwitchBladeSDK2Library.AppEventCallback
import net.heroicefforts.switchblade.SwitchBladeSDK2Library.DynamicKeyState
import net.heroicefforts.switchblade.SwitchBladeSDK2Library.GestureCallback
import net.heroicefforts.switchblade.SwitchbladeState.KeyState

println 'Hello'


def keyStateCallback = new DynamicKeyState() {
	public int invoke(int key, int keyState) {
		println "Key state change $key:$keyState"
		return 0
	}
}

def appEventCallback = new AppEventCallback() {
	public int invoke(int appEvent, int wparam, int lparam) {
		println "App event $appEvent"
		return 0
	}
}

def gestureCallback = new GestureCallback() {
	public int invoke(int gestureEvent, int dwParams, short x, short y, short z) {
		println "Gesture event $gestureEvent:($dwParams, $x, $y, $z)"
		return 0
	}
}

def SwitchbladeState parseToState(String json) {
	def slurper = new JsonSlurper().parseText(json)
	def states = slurper.keyStates.collectEntries { k, v ->
		[(k as Integer):new KeyState(upImagePath:v.upImage, onClick:{
			println "Key $k clicked."
		})]
	}
	return new SwitchbladeState(states)
}

SwitchbladeDriver driver = new SwitchbladeDriver() 
try {
	SwitchbladeState state = parseToState("""{"keyStates":{"1":{"upImage":"C:/work/dev/java/workspace/switchblade-component/resources/christmas_pudding.png"}, "2":{"upImage":"C:/work/dev/java/workspace/switchblade-component/resources/snowman.png"}}}""")//new SwitchbladeState()
//	state.setKey1State('C:/work/dev/java/workspace/switchblade-component/resources/snowman.png', {
//		println "Key 1 clicked."
//	})
	println new JsonBuilder(state)
	driver.start(state)
	Thread.sleep(10000)
}
catch(Throwable t) {
	t.printStackTrace()
}
finally {
	driver.shutdown()
}
