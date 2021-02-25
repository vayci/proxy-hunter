package me.olook.proxyhunter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Red
 * @date 2021-02-25 14:13
 */
@ConfigurationProperties(prefix = "hunter")
public class ProxyHunterProperties {

    private ProxyHunterProperties.Task task = new ProxyHunterProperties.Task();

    private ProxyHunterProperties.Vpn vpn = new ProxyHunterProperties.Vpn();

    private ProxyHunterProperties.Pool pool = new ProxyHunterProperties.Pool();

    private ProxyHunterProperties.Sentry sentry = new ProxyHunterProperties.Sentry();

    public static class Task{

        private int duration = 60;

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }
    }

    public static class Sentry{

        private boolean enable = true;

        private int threshold = 6;

        private String token;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public int getThreshold() {
            return threshold;
        }

        public void setThreshold(int threshold) {
            this.threshold = threshold;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public static class Pool{

        private String name = "proxypool";

        private int limit = 200;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }
    }

    public static class Vpn{

        private boolean enable = true;

        private String host = "127.0.0.1";

        private int port = 10809;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Vpn getVpn() {
        return vpn;
    }

    public void setVpn(Vpn vpn) {
        this.vpn = vpn;
    }

    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }

    public Sentry getSentry() {
        return sentry;
    }

    public void setSentry(Sentry sentry) {
        this.sentry = sentry;
    }
}
