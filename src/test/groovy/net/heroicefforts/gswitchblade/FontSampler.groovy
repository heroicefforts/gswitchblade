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
import java.awt.Font
import java.awt.Graphics2D
import java.awt.GraphicsEnvironment

import net.heroicefforts.gswitchblade.SwitchbladeState.KeyState

import org.slf4j.LoggerFactory

/**
 * This script shows samples of lorem ipsum on the Switchblade widget in various system fonts and sizes.  
 * It demonstrates using the SwitchbladeDriver to connect to the Switchblade, set key handlers,
 * and draw on the widget.
 */
def log = LoggerFactory.getLogger(FontSampler)
def fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()
def currentIdx = 0
def fontSize = 32
def running = true

SwitchbladeDriver driver = new SwitchbladeDriver()

def renderText = {
	def fontName = fonts[currentIdx % fonts.length]
	Font font = Font.decode("$fontName-$fontSize");
	def margin = 5
	log.debug "Font $font"
	driver.renderToTouchPad({ Graphics2D g2, int w, int h ->
		g2.setColor(Color.GREEN)
		g2.draw3DRect(0, 0, w-1, h-1, true)
		def text = "$fontName @ $fontSize pixels.\n\nLorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
		g2.drawText(text, "$fontName-$fontSize", margin, margin, w - 2*margin, h - 2*margin)
	})
} 

SwitchbladeState state = new SwitchbladeState([
	1:new KeyState(upImagePath:'./src/test/resources/icons/back.png', onClick: { 
		if(currentIdx > 0)
			--currentIdx
		renderText()
	}),
	2:new KeyState(upImagePath:'./src/test/resources/icons/next.png', onClick: {
		++currentIdx
		renderText()
	}),
	3:new KeyState(upImagePath:'./src/test/resources/icons/upload.png', onClick: {
		fontSize += 2
		renderText()
	}),
	4:new KeyState(upImagePath:'./src/test/resources/icons/download.png', onClick: {
		if(fontSize > 2)
			fontSize -= 2
		renderText()
	}),
	5:new KeyState(upImagePath:'./src/test/resources/icons/delete.png', onClick: {
		running = false
	})
])

driver.with(state, {
	renderText()

	while(running)
		Thread.sleep(500)
})
