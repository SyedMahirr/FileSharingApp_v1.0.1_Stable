package com.filesharingapp.utils;

import java.io.IOException;
import java.net.*;

/**
 * NetworkUtil
 * -----------
 * Small helpers for:
 * - finding local IP,
 * - checking if a port is free,
 * - checking if a host:port is reachable.
 */
public final class NetworkUtil {

    private NetworkUtil() {
    }

    /** Find a non-loopback local IP address to show to user. */
    public static String findLocalIp() {
        try {
            // Best effort: use local host.
            InetAddress local = InetAddress.getLocalHost();
            if (!local.isLoopbackAddress()) {
                return local.getHostAddress();
            }
        } catch (Exception ignore) {
        }

        // Fallback: try all interfaces.
        try {
            for (NetworkInterface nif : java.util.Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!nif.isUp() || nif.isLoopback()) {
                    continue;
                }
                for (InetAddress addr : java.util.Collections.list(nif.getInetAddresses())) {
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            LoggerUtil.warn("Unable to detect local IP: " + e.getMessage());
        }

        // Last resort.
        return "127.0.0.1";
    }

    /** Check if a port is free on this machine. */
    public static boolean isPortFree(int port) {
        try (ServerSocket ignored = new ServerSocket(port)) {
            ignored.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /** Check if host:port is reachable within timeout. */
    public static boolean canConnect(String host, int port, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
