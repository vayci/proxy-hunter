package me.olook.proxyhunter.core.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.olook.proxyhunter.ProxyHunterProperties;
import me.olook.proxyhunter.core.ProxyProvider;
import me.olook.proxyhunter.util.DingTalkNotice;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHost;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhaohw
 * @date 2020-02-05 23:35
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProxyListProvider implements ProxyProvider {

    private final DingTalkNotice dingTalkNotice;
    private final ProxyHunterProperties properties;
    private static final String BASE_URL = "https://proxy-list.org/english/index.php";

    @Override
    public String requestForPayload(Integer index) {

        String url = index == 1 ? BASE_URL : BASE_URL + "?p="+index;
        ProxyHunterProperties.Vpn vpn = properties.getVpn();
        HttpClient httpClient;
        if(vpn.isEnable()){
            httpClient = HttpClient.create()
                    .proxy(ops -> ops.type(reactor.netty.transport.ProxyProvider.Proxy.HTTP)
                            .host(vpn.getHost()).port(vpn.getPort()));
        }else{
            httpClient = HttpClient.create();
        }
        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        WebClient client = WebClient.builder().clientConnector(connector).build();

        Mono<String> mono = client.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatus::isError,response -> {
                    dingTalkNotice.send(response.statusCode().getReasonPhrase());
                    return null;
                })
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(10))
                .onErrorResume(throwable -> Mono.just("Error Request: "+throwable.getMessage()));;
        return mono.block();
    }

    @Override
    public List<HttpHost> resolveProxy(String payload) {
        List<HttpHost> result = new ArrayList<HttpHost>();
        String reg = "Proxy\\('(.*?)'\\)";
        Matcher m = Pattern.compile(reg).matcher(payload);
        while (m.find()) {
            String r = m.group(1);
            String s = new String(Base64.decodeBase64(r));
            String[] split = s.split(":");
            HttpHost httpHost = new HttpHost(split[0], Integer.parseInt(split[1]));
            result.add(httpHost);
        }
        return result;
    }

}
