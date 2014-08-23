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
import java.awt.FontMetrics
import java.awt.Graphics2D
import java.awt.GraphicsEnvironment
import java.awt.font.FontRenderContext
import java.awt.font.LineBreakMeasurer
import java.awt.font.TextAttribute
import java.awt.font.TextLayout
import java.text.AttributedCharacterIterator
import java.text.AttributedString

public void testStartupShutdownSeries() {
	SwitchbladeDriver driver = new SwitchbladeDriver();
	for(int i = 0; i < 10; i++) {
		driver.start(null);
		Thread.sleep 5000
		driver.shutdown();
	}
}

public void testJNAStartupShutdownSeries() {
	SwitchBladeSDK2Library sdk = SwitchBladeSDK2Library.INSTANCE
	for(int i = 0; i < 10; i++) {
		def res = sdk.RzSBStart()
		println "Start $res"
		Thread.sleep(10000)
		res = sdk.RzSBStop()
		println "Stop $res"
		Thread.sleep(10000)
		println "Finished sequence $i"
	}
} 

testRender();