package org.example.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.WorkerDto;
import org.example.dto.LaunchDto;
import org.example.dto.StatusDto;
import org.example.service.UiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UiController {
  private final UiService uiService;

  @GetMapping("/status/worker")
  List<StatusDto> workerStatus() {
    return uiService.workerStatus();
  }

  @GetMapping("/status/strategy")
  List<StatusDto> strategyStatus() {
    return uiService.strategyStatus();
  }

  @PostMapping("/launch")
  LaunchDto launch(@Valid @RequestBody LaunchDto dto) {
    return uiService.launch(dto);
  }

  @GetMapping("/params")
  List<WorkerDto> getWorkers() {
    return uiService.getWorkers();
  }
}
