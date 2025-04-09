package org.example.repo;

import org.example.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker, Long> {
  boolean existsByName(String s);

  Optional<Worker> findByName(String s);
}
