package com.chriniko.kafka.example.config;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class WorkersConfig {

    @Bean(destroyMethod = "shutdown", autowire = Autowire.BY_NAME)
    public ExecutorService basicWorkers() {
        return Executors.newFixedThreadPool(4);
    }

}
