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

	static {
		BufferedImage.metaClass.reset = {
			Graphics2D g2D = delegate.getGraphics()
			g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f))
			Rectangle2D.Double rect = new Rectangle2D.Double(0,0,delegate.width,delegate.height)
			g2D.fill(rect)
			g2D.dispose()
		}
		
		Graphics2D.metaClass.drawText = { String text, String fontDesc, int x, int y, int w, int h ->
			Font font = Font.decode(fontDesc);
			def lines = text.split('\r?\n').collect {
				if(it.trim().length() == 0)
					return (AttributedCharacterIterator) null
				else {
					def attStr = new AttributedString(it)
					attStr.addAttribute(TextAttribute.FONT, font)
					return attStr
				}
			}
			
			delegate.drawText(lines, x, y, w, h)
		}
			
		Graphics2D.metaClass.drawText = { List<AttributedString> attStrings, int x, int y, int w, int h ->
			Graphics2D graphics = delegate
			FontRenderContext frc = graphics.getFontRenderContext()
			def lines = attStrings.collectEntries { 
				if(!it)
					return [(it):0]
				else {
					def tabCount = 0
					def iter = it.getIterator()
					for(char c = iter.first(); c != AttributedCharacterIterator.DONE; c = iter.next()) {
						if(c == '\t')
							++tabCount
					}
			   
					return [(it.getIterator()):tabCount]
				}
			}

			float verticalPos = y;
//			AttributedString styledString = new AttributedString(text)
//			AttributedCharacterIterator styledText = styledString.getIterator()
			
			lines.each { AttributedCharacterIterator styledText, int tabCount ->
				if(styledText == null) {
					verticalPos += fontMetrics.getHeight()
					return
				} 			
				
				float leftMargin = x, rightMargin = x+w;
				FontMetrics fontMetrics = graphics.getFontMetrics(styledText.getAttribute(TextAttribute.FONT));
				def tabPixels = fontMetrics.stringWidth('    ')
				float[] tabStops = (1..w / tabPixels).collect { x + tabPixels * it };
			
				// assume styledText is an AttributedCharacterIterator, and the number
				// of tabs in styledText is tabCount
			
				int[] tabLocations = new int[tabCount+1];
			
				int i = 0;
				for (char c = styledText.first(); c != styledText.DONE; c = styledText.next()) {
					if (c == '\t') {
						tabLocations[i++] = styledText.getIndex();
					}
				}
				tabLocations[tabCount] = styledText.getEndIndex() - 1;
			
				// Now tabLocations has an entry for every tab's offset in
				// the text.  For convenience, the last entry is tabLocations
				// is the offset of the last character in the text.
			
				LineBreakMeasurer measurer = new LineBreakMeasurer(styledText, frc);
				int currentTab = 0;
			
				while (measurer.getPosition() < styledText.getEndIndex()) {
			
					// Lay out and draw each line.  All segments on a line
					// must be computed before any drawing can occur, since
					// we must know the largest ascent on the line.
					// TextLayouts are computed and stored in a Vector;
					// their horizontal positions are stored in a parallel
					// Vector.
			
					// lineContainsText is true after first segment is drawn
					boolean lineContainsText = false;
					boolean lineComplete = false;
					float maxAscent = 0, maxDescent = 0;
					float horizontalPos = leftMargin;
					Vector layouts = new Vector(1);
					Vector penPositions = new Vector(1);
			
					while (!lineComplete) {
						float wrappingWidth = rightMargin - horizontalPos;
						TextLayout layout =
								measurer.nextLayout(wrappingWidth,
													tabLocations[currentTab]+1,
													lineContainsText);
			
						// layout can be null if lineContainsText is true
						if (layout != null) {
							layouts.addElement(layout);
							penPositions.addElement(new Float(horizontalPos));
							horizontalPos += layout.getAdvance();
							maxAscent = Math.max(maxAscent, layout.getAscent());
							maxDescent = Math.max(maxDescent,
								layout.getDescent() + layout.getLeading());
						} else {
							lineComplete = true;
						}
			
						lineContainsText = true;
			
						if (measurer.getPosition() == tabLocations[currentTab]+1) {
							currentTab++;
						}
			
						if (measurer.getPosition() == styledText.getEndIndex())
							lineComplete = true;
						else if (horizontalPos >= tabStops[tabStops.length-1])
							lineComplete = true;
			
						if (!lineComplete) {
							// move to next tab stop
							int j;
							for (j=0; horizontalPos >= tabStops[j]; j++) {}
							horizontalPos = tabStops[j];
						}
					}
			
					verticalPos += maxAscent;
			
					Enumeration layoutEnum = layouts.elements();
					Enumeration positionEnum = penPositions.elements();
			
					// now iterate through layouts and draw them
					while (layoutEnum.hasMoreElements()) {
						TextLayout nextLayout = (TextLayout) layoutEnum.nextElement();
						Float nextPosition = (Float) positionEnum.nextElement();
						nextLayout.draw(graphics, nextPosition.floatValue(), verticalPos);
					}
			
					verticalPos += maxDescent;
				}
			}
		}
	}
	
}