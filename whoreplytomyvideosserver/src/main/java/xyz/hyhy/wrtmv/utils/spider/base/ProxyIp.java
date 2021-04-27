package xyz.hyhy.wrtmv.utils.spider.base;

import java.util.Objects;

public class ProxyIp {
    private String ip;
    private int port;
    private int exact;

    public ProxyIp() {

    }

    public ProxyIp(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getExact() {
        return exact;
    }

    public void setExact(int exact) {
        this.exact = exact;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProxyIp)) return false;
        ProxyIp proxyIp = (ProxyIp) o;
        return port == proxyIp.port &&
                ip.equals(proxyIp.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}
