package com.pasenger.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Pasenger on 2015/1/6.
 */
@SpringBootApplication
@EnableConfigurationProperties
@EnableAsync
@EnableScheduling
public class Application {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
//        Set<Object> sourcesSet = new HashSet<Object>();
//        sourcesSet.add("classpath:applicationContext.xml");
//        sourcesSet.add("classpath:applicationContext-mae-service-consumer.xml");
//
//        application.setSources(sourcesSet);

        application.run(args);
    }
}
