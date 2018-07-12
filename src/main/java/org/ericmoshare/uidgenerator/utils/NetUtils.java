package org.ericmoshare.uidgenerator.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

/**
 * @author eric.mo
 * @since 2018/7/11
 */
public class NetUtils {

    private static final Logger logger = LoggerFactory.getLogger(NetUtils.class);

    public NetUtils() throws UnknownHostException {
    }

    public static String getMacString() {
        InetAddress ip;
        try {

            ip = InetAddress.getLocalHost();
            logger.debug("Current IP address : " + ip.getHostAddress());

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            logger.debug("Current MAC address: " + sb.toString());

            return sb.toString();
        } catch (Exception e) {
            logger.error("获取mac出错", e);

        }
        return "";
    }

    public static String getIp() throws UnknownHostException {
        return Inet4Address.getLocalHost().getHostAddress();
    }

    public static String getMacAndIp() {
        try {
            return getMacString() + "_" + getIp();
        } catch (UnknownHostException e) {
            return getMacString();
        }
    }
}
