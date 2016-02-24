package base.md;

import java.io.Serializable;

/**
 * Created by Mr-yang on 16-1-9.
 */
public class MdPos implements Serializable {

    private static final long serialVersionUID = -1L;

    private String ip;
    private Integer port;
    private Long dCode;

    public MdPos() {
    }

    public MdPos(String ip, Integer port, Long dCode) {
        this.ip = ip;
        this.port = port;
        this.dCode = dCode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Long getdCode() {
        return dCode;
    }

    public void setdCode(Long dCode) {
        this.dCode = dCode;
    }

    @Override
    public String toString() {
        return "MdPos{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", dCode='" + dCode + '\'' +
                '}';
    }
}
