package base.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Created by Mr-yang on 16-2-23.
 */
public class IpTool {
    private static Logger logger = LoggerFactory.getLogger("IpTool");

    public static String getMachineIP() {
        try {
            String hostIP = InetAddress.getLocalHost().getHostAddress();
            if (!hostIP.equals("127.0.0.1")) {
                return hostIP;
            }

            Enumeration<NetworkInterface> nInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (nInterfaces.hasMoreElements()) {
                Enumeration<InetAddress> inetAddresses = nInterfaces
                        .nextElement().getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    String address = inetAddresses.nextElement()
                            .getHostAddress();
                    if (!address.equals("192.168.0.255") && address.indexOf("192.168.0") != -1) {
                        return address;
                    }
                }
            }
        } catch (UnknownHostException e1) {
            logger.error("Error = " + e1.getMessage());
        } catch (SocketException e1) {
            logger.error("Error = " + e1.getMessage());
        }
        return null;
    }
}
