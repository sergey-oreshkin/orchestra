package org.example.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableConfigurationProperties
@EnableScheduling
@EnableAsync
public class CommonConfig {

  @Bean
  ExecutorService scheduleExecutor() {
    return Executors.newFixedThreadPool(10);
  }
}
