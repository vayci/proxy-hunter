package me.olook.proxyhunter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.olook.proxyhunter.core.ProxyChecker;
import me.olook.proxyhunter.core.ProxyProvider;
import me.olook.proxyhunter.core.ProxySink;
import me.olook.proxyhunter.util.DingTalkNotice;
import org.apache.http.HttpHost;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

/**
 * @author zhaohw
 * @date 2019-05-06 16:58
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProxyHuntTask implements Runnable{

    private final ProxyProvider proxyProvider;
    private final ProxyChecker proxyChecker;
    private final ProxySink proxySink;
    private final DingTalkNotice dingTalkNotice;
    private final RedisTemplate redisTemplate;
    private final ProxyHunterProperties properties;
    private final static String ERROR_REQUEST_COUNT = "Error.Request";

    @Override
    public void run() {
        if(!isPoolNotFull()){ return; }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.debug("{} start",proxyProvider.getClass().getName());
        IntStream.rangeClosed(1,10).parallel().forEach(page->{
            String payload = proxyProvider.requestForPayload(page);
            if(payload.contains("Error Request")){
                log.error("{}",payload);
                notice(payload);
                return;
            }
            List<HttpHost> hosts = proxyProvider.resolveProxy(payload);
            log.debug("page {}  {}",page,hosts);
            CompletableFuture.runAsync(()->{
                hosts.parallelStream().forEach(host->{
                    proxyChecker.check(host,proxySink);
                });
            });
        });
        stopWatch.stop();
        log.debug("{} end, cost: {} ms",proxyProvider.getClass().getName(),stopWatch.getTotalTimeMillis());
    }

    private boolean isPoolNotFull(){
        int limit = properties.getPool().getLimit();
        if(limit <= 0){ return true; }
        Long size = redisTemplate.opsForList().size(properties.getPool().getName());
        log.info("active proxy pool size: {} , threshold: {}",size,limit);
        return size != null && size <= limit;

    }

    private synchronized void notice(String error){
        ProxyHunterProperties.Sentry sentry = properties.getSentry();
        if(sentry.isEnable()){
            Long increment = redisTemplate.opsForValue().increment(ERROR_REQUEST_COUNT);
            redisTemplate.expire(ERROR_REQUEST_COUNT, Duration.ofSeconds(30));
            if(increment != null && increment > sentry.getThreshold()){
                redisTemplate.delete(ERROR_REQUEST_COUNT);
                dingTalkNotice.send(error);
            }
        }
    }
}
