package org.example.service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.example.config.WorkerConfig;
import org.example.dto.ParamDto;
import org.example.dto.StatusDto;
import org.example.entity.Strategy;
import org.example.entity.Worker;
import org.example.entity.WorkerParam;
import org.example.repo.ParamsRepository;
import org.example.repo.StrategyRepository;
import org.example.repo.WorkerRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkerService {
  private final WorkerConfig config;
  private final WorkerRepository repository;
  private final ClientService clientService;
  private final StrategyRepository strategyRepository;
  private final ParamsRepository paramsRepository;

  @Async(value = "scheduleExecutor")
  @Scheduled(fixedDelay = 5000)
  @Transactional
  void updateStatus() {
    config.workers.values().forEach(this::updateStatus);
  }

  void updateStatus(WorkerConfig.Config config) {
    Worker worker =
        repository
            .findByName(config.name)
            .orElseThrow(() -> new RuntimeException("Нифига не нашли!"));
    worker.getStrategies().forEach(s -> s.setIsRunning(false));
    try {
      List<StatusDto> statuses = clientService.status(config);
      statuses.forEach(
          status -> {
            strategyRepository
                .findById(status.getId())
                .ifPresentOrElse(
                    s -> {
                      s.setIsRunning(status.getIsRunning());
                      s.setIsSuccess(status.getIsSuccess());
                      strategyRepository.save(s);
                    },
                    () -> {
                      Strategy strategy = getStrategy(status);
                      strategy.setWorker(worker);
                      strategyRepository.save(strategy);
                    });
          });
    } catch (Exception e) {
      worker.setIsReady(false);
      repository.save(worker);
    }
  }

  @EventListener(ApplicationReadyEvent.class)
  @Transactional
  void init() {
    Set<String> newWorkers = config.workers.keySet();
    List<Worker> workers = repository.findAll();
    List<Worker> toDelete =
        workers.stream().filter(worker -> newWorkers.contains(worker.getName())).toList();
    repository.deleteAll(toDelete);
    newWorkers.forEach(
        strategy -> {
          if (repository.existsByName(strategy)) {
            update(strategy);
            return;
          }
          create(strategy);
        });
  }

  private void create(String strategy) {
    boolean isReady = clientService.health(config.workers.get(strategy));
    Worker worker = new Worker();
    worker.setName(strategy);
    if (!isReady) {
      worker.setIsReady(false);
      repository.save(worker);
      return;
    }
    List<ParamDto> params = clientService.params(config.workers.get(strategy));
    worker.setIsReady(true);
    worker.setParams(params.stream().map(this::getWorkerParam).toList());
    worker.getParams().forEach(it -> it.setWorker(worker));
    repository.save(worker);
  }

  private void update(String strategy) {
    Worker worker =
        repository.findByName(strategy).orElseThrow(() -> new RuntimeException("Нифига не нашли!"));
    boolean isReady = clientService.health(config.workers.get(strategy));
    List<ParamDto> params = clientService.params(config.workers.get(strategy));
    List<StatusDto> statuses = clientService.status(config.workers.get(strategy));
    worker.setIsReady(isReady);
    paramsRepository.deleteAll(worker.getParams());
    worker.getParams().addAll(params.stream().map(this::getWorkerParam).toList());
    statuses.forEach(
        status -> {
          strategyRepository
              .findById(status.getId())
              .ifPresentOrElse(
                  s -> {
                    s.setIsSuccess(status.getIsSuccess());
                    s.setIsRunning(status.getIsRunning());
                    strategyRepository.save(s);
                  },
                  () -> {
                    Strategy entity = getStrategy(status);
                    entity.setWorker(worker);
                    strategyRepository.save(entity);
                  });
        });
  }

  private Strategy getStrategy(StatusDto dto) {
    Strategy strategy = new Strategy();
    strategy.setName(dto.getName());
    strategy.setIsRunning(dto.getIsRunning());
    strategy.setIsSuccess(dto.getIsSuccess());
    strategy.setLastMessage("Инициализация!");
    return strategy;
  }

  private WorkerParam getWorkerParam(ParamDto paramsDto) {
    WorkerParam workerParam = new WorkerParam();
    workerParam.setName(paramsDto.getName());
    workerParam.setOptional(paramsDto.getOptional());
    return workerParam;
  }
}
