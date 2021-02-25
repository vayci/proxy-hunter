package me.olook.proxyhunter.core;

import org.apache.http.HttpHost;

import java.util.function.Consumer;

public interface ProxyChecker {

    void check(HttpHost httpHost, Consumer<HttpHost> consumer);
}
