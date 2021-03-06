/**
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
package net.heroicefforts.gswitchblade;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef;

/**
 * JNA structure for holding image data sent to the Switchblade LCDs.
 * 
 * @author jevans
 *
 */
public class BufferParams extends Structure {
    public static class ByValue extends BufferParams implements Structure.ByValue { }
    public static class ByReference extends BufferParams implements Structure.ByReference { }

    public int pixelType = 0;
    public WinDef.DWORD dataSize;
    public Pointer ptrData;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("pixelType", "dataSize", "ptrData");
    }
}
