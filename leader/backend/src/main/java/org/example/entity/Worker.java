package org.example.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Worker {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private Boolean isReady;

  @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<WorkerParam> params;

  @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Strategy> strategies;
}
