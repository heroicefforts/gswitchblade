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

import net.heroicefforts.gswitchblade.GestureListener.Direction
import net.heroicefforts.gswitchblade.GestureListener.Rotation
import net.heroicefforts.gswitchblade.GestureListener.Zoom

/**
 * A no-op class that captures the driver reference.
 * 
 * @author jevans
 *
 */
abstract class AbstractGestureListener implements GestureListener {
	protected SwitchbladeDriver driver
	
	@Override
	public void setDriver(SwitchbladeDriver driver) {
		this.driver = driver
	}

	@Override
	public void onPress(int touchPoints, short x, short y) {
	}

	@Override
	public void onTap(short x, short y) {
	}

	@Override
	public void onFlick(int touchPoints, Direction dir) {
	}

	@Override
	public void onZoom(Zoom zoom) {
	}

	@Override
	public void onRotate(Rotation rotation) {
	}

	@Override
	public void onMove(short x, short y) {
	}

	@Override
	public void onRelease(int touchPoints, short x, short y) {
	}

}
