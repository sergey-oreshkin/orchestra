package org.example.worker;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class WorkerWrapper implements Runnable {
  private final Runnable delegate;
  private final AtomicBoolean isSuccess;
  private final String name;

  @Override
  public void run() {
    try {
      delegate.run();
      isSuccess.getAndSet(true);
      log.info("Процесс {} успешно завершен", name);
    } catch (Throwable t) {
      // логика аварийного завершения
      log.error(String.format("Процесс %s завершился с ошибкой ", name), t);
      isSuccess.getAndSet(false);
    }
  }
}
