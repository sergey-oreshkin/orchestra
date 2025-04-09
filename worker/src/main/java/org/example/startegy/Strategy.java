package org.example.startegy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.example.dto.ParamDto;

public interface Strategy {
  String getName();

  List<ParamDto> getParams();

  void start(Map<String, String> params, AtomicBoolean start);
}
