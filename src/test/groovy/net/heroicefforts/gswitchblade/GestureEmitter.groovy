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

import java.awt.Color
import java.awt.Graphics2D

import net.heroicefforts.groovy.util.BoundedListDecorator
import net.heroicefforts.gswitchblade.GestureListener.Direction;
import net.heroicefforts.gswitchblade.GestureListener.Rotation;
import net.heroicefforts.gswitchblade.GestureListener.Zoom;
import net.heroicefforts.gswitchblade.SwitchbladeState.KeyState

import org.slf4j.LoggerFactory

/**
 * This script dumps gestures to the Switchblade widget.
 */
def log = LoggerFactory.getLogger(FontSampler)
def running = true
def gestureMsgs = new BoundedListDecorator(new LinkedList<String>(), 15)

SwitchbladeDriver driver = new SwitchbladeDriver(new GestureListener() {
	SwitchbladeDriver driver
	void onPress(int touchPoints, short x, short y) { add("Press tp:$touchPoints, x:$x, y:$y") }
	void onTap(short x, short y) { add("Tap x:$x, y:$y") }
	void onFlick(int touchPoints, Direction dir) { add("Flick $dir tp:$touchPoints") }
	void onZoom(Zoom zoom) { add("Zoom $zoom") }
	void onRotate(Rotation rotation) { add("Rotate $rotation") }
	void onMove(short x, short y) { add("Move x:$x, y:$y") }
	void onRelease(int touchPoints, short x, short y) { add("Release tp:$touchPoints, x:$x, y:$y") }
	
	void setSwitchbladeDriver(SwitchbladeDriver driver) {
		this.driver = driver
	}
	
	private void add(String gestureMsg) {
		gestureMsgs << gestureMsg
		renderGestures()
	}
	
	private renderGestures() {
		def margin = 5
		driver.renderToTouchPad({ Graphics2D g2, int w, int h ->
			g2.setColor(Color.GREEN)
			g2.draw3DRect(0, 0, w-1, h-1, true)
			def text = gestureMsgs.join('\n')
			g2.drawText(text, 'Helvetica-24', margin, margin, w - 2*margin, h - 2*margin)
		})
	}
	
})

SwitchbladeState state = new SwitchbladeState([
	5:new KeyState(upImagePath:'./src/test/resources/icons/delete.png', onClick: {
		running = false
	})
])

driver.with(state) {
	while(running)
		Thread.sleep(500)
}
