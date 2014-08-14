package net.heroicefforts.gswitchblade

import groovy.json.JsonBuilder;
import groovy.transform.Canonical
import groovy.transform.ToString

/**
 * Aggregates the states of Switchblade keys and widget.<br/>
 * <br/>
 * Defines the following dynamic method:<br/>
 * <blockquote>
 * <span style="color:limegreen;">setKey<i>N</i>State(String pathToKeyImage, Closure onClick)</span>, 
 * where N is 1 to ...
 * </blockquote>
 *  
 * @author jevans
 *
 */
@Canonical
@ToString(includeNames=true)
class SwitchbladeState {

	Map<Integer, KeyState> states = [:]
	String widgetUrl
	String widgetScript
	
	
	public SwitchbladeState(Map<Integer, KeyState> states) {
		this.states = states
	}
	
	private void setKeyState(int key, KeyState state) {
		states[key] = state
	}	

	/**
	 * Contains the image and key event handlers for association with a Switchblade key.
	 *  
	 * @author jevans
	 *
	 */
	@Canonical
	@ToString(includeNames=true)
	private static class KeyState {
		String upImagePath
		Closure onClick
		Closure onLongPress
	}

	public void setWidget(String url, String script) {
		this.widgetUrl = widgetUrl
		this.widgetScript = script
	}
		
	static {
		SwitchbladeState.metaClass.methodMissing = {String name, args ->
			def keyImgMatcher = (name =~ /setKey([1-9]|10)State/)
			if(keyImgMatcher.matches() && args.length == 2) {
				delegate.metaClass."$name" = {String imagePath, Closure onClick -> delegate.setKeyState(keyImgMatcher[0][1] as Integer, new KeyState(imagePath, onClick)) }
				delegate.invokeMethod("$name", args)
			}
			else throw new MissingMethodException(name, delegate.class, args)
		}
	}

}
