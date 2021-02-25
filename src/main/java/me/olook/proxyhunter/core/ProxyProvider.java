package me.olook.proxyhunter.core;

import org.apache.http.HttpHost;

import java.util.List;

/**
 * 代理提供者
 * @author Red
 */
public interface ProxyProvider {

    /**
     * 请求代理数据
     * @return HTML/JSON/TXT
     */
    String requestForPayload(Integer index);

    /**
     * 从载荷中解析代理数据
     * @param payload 载荷
     * @return 代理信息
     */
    List<HttpHost> resolveProxy(String payload);
}
