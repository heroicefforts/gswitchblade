package net.heroicefforts.switchblade

import groovy.json.JsonBuilder;
import groovy.transform.Canonical
import groovy.transform.ToString

@Canonical
@ToString(includeNames=true)
class SwitchbladeState {

	Map<Integer, KeyState> states = [:]
	
	private void setKeyState(int key, KeyState state) {
		states[key] = state
	}	

	@Canonical
	@ToString(includeNames=true)
	private static class KeyState {
		String upImagePath
		Closure onClick
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
