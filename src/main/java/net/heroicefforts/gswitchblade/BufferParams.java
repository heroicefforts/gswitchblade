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
