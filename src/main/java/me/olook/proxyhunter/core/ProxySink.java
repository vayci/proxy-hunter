package me.olook.proxyhunter.core;

import org.apache.http.HttpHost;

import java.util.function.Consumer;

public interface ProxySink extends Consumer<HttpHost> {

}
