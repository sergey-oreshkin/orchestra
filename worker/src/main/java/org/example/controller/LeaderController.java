package org.example.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.LaunchDto;
import org.example.dto.ParamDto;
import org.example.dto.StatusDto;
import org.example.service.LaunchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class LeaderController {

  private final LaunchService launchService;

  @PostMapping("/launch")
  LaunchDto launch(@Valid @RequestBody LaunchDto dto) {
    return launchService.launch(dto);
  }

  @GetMapping("/status")
  List<StatusDto> status() {
    return launchService.status();
  }

  @GetMapping("/params")
  List<ParamDto> params() {
    return launchService.params();
  }
}
