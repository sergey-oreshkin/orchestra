package org.example.repo;

import org.example.entity.WorkerParam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParamsRepository extends JpaRepository<WorkerParam, Long> {}
