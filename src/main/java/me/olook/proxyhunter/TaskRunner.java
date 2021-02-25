package me.olook.proxyhunter;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Red
 * @date 2021-02-23 15:47
 */
@Component
@RequiredArgsConstructor
public class TaskRunner implements ApplicationRunner {

    private final ProxyHuntTask proxyHuntTask;
    private final ProxyHunterProperties properties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(
                proxyHuntTask, 0, properties.getTask().getDuration(), TimeUnit.SECONDS);
    }
}
