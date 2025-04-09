package org.example.startegy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.ParamDto;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FirstStrategy implements Strategy {

  @Override
  public String getName() {
    return "first";
  }

  @Override
  public List<ParamDto> getParams() {
    return List.of(ParamDto.builder().name("firstParam").optional(true).build());
  }

  @Override
  public void start(Map<String, String> params, AtomicBoolean start) {
    while (start.get()) {
      log.info("Работает стратегия {} с параметрами {}", getName(), params);
      try {
        Thread.sleep(1800);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException(e);
      }
    }
    // логика завершения
  }
}
