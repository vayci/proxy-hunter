package me.olook.proxyhunter.core.checker;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.olook.proxyhunter.core.ProxyChecker;
import me.olook.proxyhunter.util.UserAgents;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpPost;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.time.Duration;
import java.util.function.Consumer;

/**
 * 通过听歌排行榜校验代理
 * @author zhaohw
 */
@Slf4j
@Component
public class PlayRecordChecker implements ProxyChecker {

    private static final String TEST_URL = "http://music.163.com/weapi/v1/play/record?" +
            "params=OMUybrnN%2B6ELSzpZkRWe231b2b9yUKz1R40sylwNkSRXly6B1gWZm95kQ2iZuB81JnOvyLbKUqII%0D%0AjZDk" +
            "Ur4xoaKu6XQLH5W7ofChQtSucSexc13PZZvrI60tuw6aIjnCmZkyt9VFfS0uCZ8dpiB11CjQ%0D%0AiHgMNitSrMl51NOJ9" +
            "4fw2UlQMcDXLZTsXj9fpYWL&encSecKey=257348aecb5e556c066de214e531faadd1c55d814f9be95fd06d6bff9f4c7" +
            "a41f831f6394d5a3fd2e3881736d94a02ca919d952872e7d0a50ebfa1769a7a62d512f5f1ca21aec60bc3819a9c3ffc" +
            "a5eca9a0dba6d6f7249b06f5965ecfff3695b54e1c28f3f624750ed39e7de08fc8493242e26dbc4484a01c76f739e135637c";

    @SneakyThrows
    @Override
    public void check(HttpHost httpHost, Consumer<HttpHost> consumer) {
        HttpPost request = new HttpPost(TEST_URL);

        HttpClient httpClient = HttpClient.create()
                .proxy(ops ->
                ops.type(reactor.netty.transport.ProxyProvider.Proxy.HTTP)
                        .host(httpHost.getHostName()).port(httpHost.getPort())).compress(true);

        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        WebClient client = WebClient.builder().clientConnector(connector).build();

        Flux<String> flux = client.post()
                .uri(URI.create(TEST_URL))
                .headers((header)->{
                    header.set(HttpHeaders.ACCEPT,"*/*");
                    header.set(HttpHeaders.ACCEPT_LANGUAGE,"zh-CN,en-US;q=0.7,en;q=0.3");
                    header.set(HttpHeaders.ACCEPT_ENCODING,"gzip,deflate,sdch");
                    header.set(HttpHeaders.CONNECTION,"keep-alive");
                    header.set(HttpHeaders.CONTENT_TYPE,"application/x-www-form-urlencoded; charset=UTF-8");
                    header.set(HttpHeaders.HOST,"music.163.com");
                    header.set(HttpHeaders.REFERER,"http://music.163.com/");
                    header.set(HttpHeaders.USER_AGENT,UserAgents.randomUserAgent());
                })
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    log.error("{} => {}",httpHost,response.statusCode().getReasonPhrase());
                    return null;
                })
                .bodyToFlux(String.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(throwable -> Flux.just("Error Request: "+throwable.getMessage()));
        flux.subscribe(res->{
            if(res == null || res.contains("Error Request")){
                log.debug("error proxy {}",httpHost);
            }else{
                consumer.accept(httpHost);
                log.info("normal proxy {}",httpHost);
            }
        });
    }

    public static void main(String[] args) {
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
        PlayRecordChecker checker = new PlayRecordChecker();
    }

}
