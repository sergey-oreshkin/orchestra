package org.example.service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.example.config.UrlConfig;
import org.example.config.WorkerConfig;
import org.example.dto.LaunchDto;
import org.example.dto.ParamDto;
import org.example.dto.StatusDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class ClientService {
  private final ConcurrentHashMap<String, RestClient> clients;
  private final Function<String, RestClient> restClientFactory;
  private final UrlConfig urlConfig;
  private final WorkerConfig workerConfig;

  public ClientService(
      Function<String, RestClient> restClientFactory,
      UrlConfig urlConfig,
      WorkerConfig workerConfig) {
    this.workerConfig = workerConfig;
    this.urlConfig = urlConfig;
    this.clients = new ConcurrentHashMap<>();
    this.restClientFactory = restClientFactory;
  }

  public boolean health(WorkerConfig.Config config) {
    try {
      getClient(config).get().uri(urlConfig.health).retrieve().body(Void.class);
      return true;
    } catch (Exception e) {
      log.error(String.format("Воркер %s не здоров", config.name), e);
      return false;
    }
  }

  public List<ParamDto> params(WorkerConfig.Config config) {
    try {
      return getClient(config)
          .get()
          .uri(urlConfig.params)
          .retrieve()
          .body(new ParameterizedTypeReference<>() {});
    } catch (Exception e) {
      log.error(String.format("Не удалось получить параметры для воркера %s", config.name), e);
      return Collections.emptyList();
    }
  }

  public List<StatusDto> status(WorkerConfig.Config config) {
    try {
      return getClient(config)
          .get()
          .uri(urlConfig.status)
          .retrieve()
          .body(new ParameterizedTypeReference<>() {});
    } catch (Exception e) {
      log.error(String.format("Не удалось получить cтатусы для воркера %s", config.name), e);
      return Collections.emptyList();
    }
  }

  public LaunchDto launch(LaunchDto dto) {
    try {
      return getClient(workerConfig.workers.get(dto.getStrategy()))
          .post()
          .uri(urlConfig.launch)
          .body(dto)
          .retrieve()
          .body(LaunchDto.class);

    } catch (Exception e) {
      log.error("Запуск не удался!", e);
      dto.setSuccess(false);
      dto.setMessage("Запуск не удался - " + e.getMessage());
      return dto;
    }
  }

  private RestClient getClient(WorkerConfig.Config config) {
    return clients.computeIfAbsent(config.name, it -> restClientFactory.apply(config.server));
  }
}
