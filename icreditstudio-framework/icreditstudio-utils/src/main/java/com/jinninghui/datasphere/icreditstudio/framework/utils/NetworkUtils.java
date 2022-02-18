package com.jinninghui.datasphere.icreditstudio.framework.utils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * ClassName: NetworkUtils <br/>
 * Description: 网络工具类
 * Date: 2019/03/07 11:07
 *
 * @author liyanhui
 */
public class NetworkUtils {

    /**
     * 网卡过滤
     */
    public enum Filter {

        /**全部*/
        ALL,

        /**在线*/
        UP,

        /**虚拟*/
        VIRTUAL,

        /**loopback*/
        LOOPBACK,

        /**物理*/
        PHYSICAL_ONLY,
        ;

        public boolean apply(NetworkInterface input) {

            if (null == input) {
                return false;
            }

            try {
                switch (this) {
                    case UP:
                        return input.isUp();
                    case VIRTUAL:
                        return input.isVirtual();
                    case LOOPBACK:
                        return input.isLoopback();
                    case PHYSICAL_ONLY:
                        byte[] hardwareAddress = input.getHardwareAddress();
                        return null != hardwareAddress && hardwareAddress.length > 0 && !input.isVirtual() && !isVmMac(hardwareAddress);
                    case ALL:
                    default:
                        return true;
                }
            } catch (SocketException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }

    public enum Radix {
        /**二进制*/
        BIN(2),

        /**10进制*/
        DEC(10),

        /** 16进制*/
        HEX(16),
        ;

        final int value;

        Radix(int radix) {
            this.value = radix;
        }
    }

    private static byte[][] invalidMacs = {
            //VMWare
            {0x00, 0x05, 0x69},
            {0x00, 0x1c, 0x14},
            {0x00, 0x0c, 0x29},
            {0x00, 0x50, 0x56},
            //VirtualBox
            {0x08, 0x00, 0x27},
            {0x0a, 0x00, 0x27},
            //Virtual-PC
            {0x00, 0x03, (byte) 0xff},
            //Hyper-V
            {0x00, 0x15, 0x50}
    };

    private static boolean isVmMac(byte[] mac) {
        if (null == mac) {
            return false;
        }
        for (byte[] invalid : invalidMacs) {
            if (invalid[0] == mac[0] && invalid[1] == mac[1] && invalid[2] == mac[2]) {
                return true;
            }
        }
        return false;
    }

    public static Set<NetworkInterface> getNICs(Filter... filters) {
        if (null == filters) {
            filters = new Filter[]{Filter.ALL};
        }

        Set<NetworkInterface> ret = new HashSet<>();
        Enumeration<NetworkInterface> networkInterfaceEnumeration;
        try {
            networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException ex) {
            throw new IllegalStateException(ex);
        }
        while (networkInterfaceEnumeration.hasMoreElements()) {
            boolean match = false;
            NetworkInterface next = networkInterfaceEnumeration.nextElement();
            for (Filter filter : filters) {
                if (!filter.apply(next)) {
                    continue;
                }
                match = true;
            }

            if (match) {
                ret.add(next);
            }
        }

        return ret;
    }

    public static String getMacAddress(NetworkInterface networkInterface, String separator) {
        try {
            return format(networkInterface.getHardwareAddress(), separator, Radix.HEX);
        } catch (SocketException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static String format(byte[] source, String separator, final Radix radix) {
        if (null == source) {
            return "";
        }

        if (null == separator) {
            separator = "";
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : source) {
            sb.append(separator).append(apply(b, radix));
        }

        return sb.length() > 0 ? sb.substring(separator.length()) : "";

    }

    public final static String getIpAddress(HttpServletRequest request) throws IOException {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();//这个是不可能拿不到值的
        }
        if (ip.length() > 15) {//多个IP时，取第一个非unknow的IP
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = (String) ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

    private static String apply(Byte input, final Radix radix) {
        return String.copyValueOf(new char[]{Character.forDigit((input & 240) >> 4, radix.value), Character.forDigit(input & 15, radix.value)});
    }
}
