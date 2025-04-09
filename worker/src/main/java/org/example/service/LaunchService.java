package org.example.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.LaunchDto;
import org.example.dto.ParamDto;
import org.example.dto.StatusDto;
import org.example.startegy.Strategy;
import org.example.worker.WorkerState;
import org.example.worker.WorkerWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LaunchService {

  private static final ScheduledExecutorService POOL = Executors.newScheduledThreadPool(2);

  @Value("${org.example.strategy.shutdown-timeout}")
  private Integer timeout;

  @Value("${org.example.strategy.name}")
  private String strategy;

  private final ConcurrentHashMap<String, WorkerState> workers;
  private final Map<String, Strategy> strategies;

  public LaunchService(List<Strategy> strategies) {
    this.workers = new ConcurrentHashMap<>();
    this.strategies =
        strategies.stream().collect(Collectors.toMap(Strategy::getName, Function.identity()));
  }

  public LaunchDto launch(LaunchDto dto) {
    if (!dto.getStrategy().startsWith(strategy)) {
      return response(dto, false, "Не та стратегия!!");
    }
    if (Boolean.TRUE.equals(dto.getStart())) {
      return start(dto);
    }
    return stop(dto);
  }

  public List<StatusDto> status() {
    return workers.values().stream().map(this::getStatus).toList();
  }

  private StatusDto getStatus(WorkerState state) {
    String[] split = state.getName().split("-");
    return StatusDto.builder()
        .name(split[0])
        .id(Long.valueOf(split[1]))
        .isRunning(state.getIsRunning().get())
        .isSuccess(state.getIsSuccess().get())
        .build();
  }

  private LaunchDto stop(LaunchDto dto) {
    String fullName = dto.getFullName();
    WorkerState state = workers.get(fullName);
    if (state == null) {
      return response(dto, false, "Процесс не найден!");
    }
    if (state.getIsRunning().get()) {
      state.getIsRunning().getAndSet(false);
      POOL.schedule(this::shutdown, timeout, TimeUnit.MILLISECONDS);
      return response(
          dto, true, "Запущена остановка процесса. О результатах сообщим дополнительно");
    }
    return response(
        dto, false, "Процесс уже был остановлен. Success = " + state.getIsSuccess().get());
  }

  private LaunchDto start(LaunchDto dto) {
    String fullName = dto.getFullName();

    if (workers.containsKey(fullName)) {
      WorkerState state = workers.get(fullName);
      if (state.getIsRunning().get()) {
        return response(dto, false, "Процесс с таким именем уже запущен..");
      }
      return response(dto, false, "Процесс с таким именем завершается..");
    }

    Strategy strategy = strategies.get(dto.getStrategy());
    if (strategy == null) {
      return response(dto, false, "Не найдена подходящая стратегия!");
    }

    AtomicBoolean isRunning = new AtomicBoolean(true);
    AtomicBoolean isSuccess = new AtomicBoolean(false);
    Thread thread =
        new Thread(
            new WorkerWrapper(
                () -> strategy.start(dto.getParams(), isRunning), isSuccess, fullName));
    WorkerState state =
        WorkerState.builder()
            .thread(thread)
            .isRunning(isRunning)
            .isSuccess(isSuccess)
            .name(fullName)
            .build();
    WorkerState previous = workers.putIfAbsent(fullName, state);
    if (previous == null) {
      thread.start();
      return response(dto, true, "Процесс успешно запущен!");
    } else {
      workers.put(fullName, previous);
      return response(dto, true, "Процесс уже запущен!");
    }
  }

  public List<ParamDto> params() {
    Strategy mainStrategy = strategies.get(strategy);
    if (mainStrategy == null) {
      throw new RuntimeException("Нет основной стратегии!");
    }
    return mainStrategy.getParams();
  }

  private void shutdown() {
    Set<String> stopped =
        workers.values().stream()
            .filter(it -> !it.getIsRunning().get())
            .map(WorkerState::getName)
            .collect(Collectors.toSet());
    stopped.forEach(
        name -> {
          WorkerState state = workers.get(name);
          if (!state.getIsSuccess().get() && state.getThread().isAlive()) {
            log.error(
                "Процесс {} не завершился за {} мс и будет завершен принудительно", name, timeout);
            state.getThread().interrupt();
          }
          workers.remove(name);
          // тут добавить вызов оркестратора с сообщением о завершении
        });
  }

  private LaunchDto response(LaunchDto dto, Boolean success, String message) {
    dto.setMessage(message);
    dto.setSuccess(success);
    return dto;
  }
}
