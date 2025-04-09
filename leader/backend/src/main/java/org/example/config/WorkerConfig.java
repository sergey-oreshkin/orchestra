package org.example.config;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
@Data
public class WorkerConfig {
  public Map<String, Config> workers;

  @Data
  public static class Config {
    public String name;
    public String server;
  }
}
