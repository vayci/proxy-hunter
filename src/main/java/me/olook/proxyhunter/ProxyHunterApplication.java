package me.olook.proxyhunter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ProxyHunterProperties.class)
public class ProxyHunterApplication {

    public static void main(String[] args) {
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
        SpringApplication.run(ProxyHunterApplication.class, args);
    }

}
