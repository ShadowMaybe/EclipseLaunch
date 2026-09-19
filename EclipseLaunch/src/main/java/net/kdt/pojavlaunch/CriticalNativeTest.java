package net.kdt.pojavlaunch;

import me.shadow.eclipselaunch.compat.annotation.CriticalNative;

public class CriticalNativeTest {
    @CriticalNative
    public static native void testCriticalNative(int arg0, int arg1);
    public static void invokeTest() {
        testCriticalNative(0, 0);
    }
}
