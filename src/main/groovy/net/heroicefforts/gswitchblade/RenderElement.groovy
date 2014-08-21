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
import com.sun.jna.platform.win32.WinDef
import java.awt.AlphaComposite
import java.awt.Font
import java.awt.FontMetrics
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.font.FontRenderContext
import java.awt.font.LineBreakMeasurer
import java.awt.font.TextAttribute
import java.awt.font.TextLayout
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.awt.image.Raster
import java.text.AttributedCharacterIterator
import java.text.AttributedString

/**
 * This class handles the graphics interface to Switchblade LCDs.
 *  
 * @author jevans
 *
 */
class RenderElement {
	private BufferedImage bi
	
	int id
	int width
	int height
	BufferParams bufferParams = new BufferParams.ByReference()
	
	/**
	 * Construct a RenderElement for a specific LCD.
	 * 
	 * @param id of the LCD
	 * @param width 
	 * @param height 
	 */
	public RenderElement(int id, int width, int height) {
		this.id = id
		this.width = width
		this.height = height
	
		bi = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_565_RGB)
			
		int pixelCount = width * height
		int bufferBytes = pixelCount * 2 /* 565 RGB - 2 byte pixels */
		bufferParams.pixelType = 0
		bufferParams.ptrData = new Memory(bufferBytes)
		bufferParams.dataSize = new WinDef.DWORD(bufferBytes)
	}
	
	/**
	 * Allows user code to draw to the LCD image buffer.  Takes a closure of the form:<br/>
	 * Closure(Graphics2D, int width, int height)
	 * @param graphicsWork  
	 * @return this
	 */
	public RenderElement draw(Closure graphicsWork) {
		bi.getGraphics().with {
			graphicsWork(it, bi.width, bi.height)
		}
		
		return this
	}
	
	/**
	 * Commits the image buffer for rendering the LCD.
	 */
	public synchronized void commit() {
		Raster r = bi.getData()
		short[] sdata = r.getDataElements(0, 0, bi.width, bi.height, null)
		bufferParams.ptrData.write(0, sdata, 0, sdata.length)
		bi.reset()
	}

}