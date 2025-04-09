package org.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "endpoints")
@Data
public class UrlConfig {
  public String health;
  public String status;
  public String launch;
  public String params;
}
