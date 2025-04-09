import { Component, OnDestroy, OnInit } from '@angular/core';
import { interval, Subject, Subscription, takeUntil, timer } from 'rxjs';
import { Worker } from '../_models';
import { WorkerService } from '../_services';
import { NgClass, NgFor } from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-worker',
  imports: [NgFor, NgClass],
  templateUrl: './worker.component.html',
  styleUrl: './worker.component.scss',
})
export class WorkerComponent implements OnInit, OnDestroy {
  destructor = new Subject();
  workers: Worker[] = [];

  constructor(private workerService: WorkerService) {}

  ngOnInit(): void {
    timer(2000, 5000)
      .pipe(takeUntil(this.destructor))
      .subscribe((ignore) =>
        this.workerService.getWorkers().subscribe((res) => (this.workers = res))
      );
  }

  ngOnDestroy(): void {
    this.destructor.next('');
  }
}
