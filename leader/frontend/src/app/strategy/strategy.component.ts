import { NgClass, NgFor } from '@angular/common';
import { Component } from '@angular/core';
import { Subject, takeUntil, timer } from 'rxjs';
import { WorkerService } from '../_services';
import { Strategy } from '../_models/strategy';

@Component({
  standalone: true,
  selector: 'app-strategy',
  imports: [NgFor, NgClass],
  templateUrl: './strategy.component.html',
  styleUrl: './strategy.component.scss',
})
export class StrategyComponent {
  destructor = new Subject();
  strategies: Strategy[] = [];

  constructor(private workerService: WorkerService) {}

  ngOnInit(): void {
    timer(2000, 5000)
      .pipe(takeUntil(this.destructor))
      .subscribe((ignore) =>
        this.workerService
          .getStrategies()
          .subscribe((res) => (this.strategies = res))
      );
  }

  ngOnDestroy(): void {
    this.destructor.next('');
  }
}
