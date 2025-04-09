package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Data;

@Data
public class LaunchDto {
  @NotNull @NotBlank private String strategy;
  private Long number;
  @NotNull private Boolean start;
  @NotNull private Map<String, String> params;
  private String message;
  private Boolean success;

  public String getFullName() {
    return String.format("%s-%d", strategy, number);
  }
}
