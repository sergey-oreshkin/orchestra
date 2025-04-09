package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class StatusDto {
  Long id;
  String name;
  Boolean isRunning;
  Boolean isSuccess;
}
