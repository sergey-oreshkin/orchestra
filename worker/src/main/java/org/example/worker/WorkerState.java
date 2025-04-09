package org.example.worker;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class WorkerState {
  private final String name;
  private final Thread thread;
  private final AtomicBoolean isRunning;
  private final AtomicBoolean isSuccess;
}
