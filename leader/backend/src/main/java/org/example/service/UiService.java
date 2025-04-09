package org.example.service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.example.WorkerDto;
import org.example.dto.LaunchDto;
import org.example.dto.StatusDto;
import org.example.entity.Strategy;
import org.example.entity.Worker;
import org.example.entity.WorkerParam;
import org.example.repo.StrategyRepository;
import org.example.repo.WorkerRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UiService {
  private final WorkerRepository repository;
  private final ClientService clientService;
  private final StrategyRepository strategyRepository;

  public LaunchDto launch(LaunchDto dto) {
    Strategy strategy = new Strategy();
    try {
      Worker worker =
          repository
              .findByName(dto.getStrategy())
              .orElseThrow(() -> new RuntimeException("Воркер не найден!"));
      if (dto.getNumber() < 0 && Boolean.TRUE.equals(dto.getStart())) {
        strategy.setName(dto.getStrategy());
        strategy.setWorker(worker);
        strategy = strategyRepository.save(strategy);
        dto.setNumber(strategy.getId());
      }
      return clientService.launch(dto);
    } catch (Exception e) {
      if(Objects.nonNull(strategy.getId())){
        strategyRepository.delete(strategy);
      }
      dto.setSuccess(false);
      dto.setMessage(e.getMessage());
      return dto;
    }
  }

  public List<StatusDto> workerStatus() {
    List<Worker> workers = repository.findAll();
    return workers.stream().map(this::getWorkerStatuses).toList();
  }

  public List<StatusDto> strategyStatus() {
    List<Strategy> strategies = strategyRepository.findAll();
    return strategies.stream().map(this::getStrategyStatuses).toList();
  }

  public List<WorkerDto> getWorkers() {
    return repository.findAll().stream().map(this::toDto).toList();
  }

  private WorkerDto toDto(Worker worker) {
    return WorkerDto.builder()
        .name(worker.getName())
        .params(worker.getParams().stream().map(WorkerParam::getName).toList())
        .build();
  }

  private StatusDto getWorkerStatuses(Worker worker) {
    return StatusDto.builder().isRunning(worker.getIsReady()).name(worker.getName()).build();
  }

  private StatusDto getStrategyStatuses(Strategy strategy) {
    return StatusDto.builder()
        .id(strategy.getId())
        .isRunning(strategy.getIsRunning())
        .name(strategy.getName())
        .isSuccess(strategy.getIsSuccess())
        .build();
  }
}
