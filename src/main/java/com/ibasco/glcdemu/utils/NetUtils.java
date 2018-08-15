package com.ibasco.glcdemu.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetUtils {
    public static String getLocalAddress(String defaultAddr) {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return defaultAddr;
        }
    }
}
