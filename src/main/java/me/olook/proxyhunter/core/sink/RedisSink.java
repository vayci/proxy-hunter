package me.olook.proxyhunter.core.sink;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.olook.proxyhunter.ProxyHunterProperties;
import me.olook.proxyhunter.core.ProxySink;
import org.apache.http.HttpHost;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author zhaohw
 */
@Slf4j
@Component
@EnableConfigurationProperties
@RequiredArgsConstructor
public class RedisSink implements ProxySink {

    private final RedisTemplate redisTemplate;
    private final ProxyHunterProperties properties;

    @Override
    public void accept(HttpHost httpHost) {
        log.debug("accept proxy {}",httpHost);
        redisTemplate.opsForList().leftPush(properties.getPool().getName(),httpHost);
    }
}
