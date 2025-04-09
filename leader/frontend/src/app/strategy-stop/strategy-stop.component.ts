import { Component, OnInit } from '@angular/core';
import { Strategy } from '../_models/strategy';
import { WorkerService } from '../_services';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Launch } from '../_models/launch';
import { NgClass, NgFor } from '@angular/common';

@Component({
  selector: 'app-strategy-stop',
  imports: [ReactiveFormsModule, NgClass, NgFor],
  templateUrl: './strategy-stop.component.html',
  styleUrl: './strategy-stop.component.scss',
})
export class StrategyStopComponent implements OnInit {
  strategies: Strategy[] = [];
  current: Strategy = {
    id: 0,
    name: '',
    isRunning: false,
    isSuccess: false,
  };
  result: Launch = {
    number: -1,
    strategy: '',
    start: null,
    params: null,
    message: '',
    success: null,
  };
  form: FormGroup = new FormGroup({ name: new FormControl('') });

  constructor(private workerService: WorkerService) {}

  ngOnInit(): void {
    this.workerService
      .getStrategies()
      .subscribe((res) => (this.strategies = res));
  }

  change() {
    const strategy = this.strategies.filter(
      (p) => p.name === this.form.controls['name'].value
    )[0];
    this.current = strategy;
  }

  stop() {
    const name: string = this.form.get('name')?.value;
    console.log(name);
    const launch: Launch = {
      number: +name.split('-')[1],
      strategy: name.split('-')[0],
      start: false,
      params: {},
      message: '',
      success: null,
    };
    console.log(launch);
    this.workerService.launch(launch).subscribe({
      next: (res) => (this.result = res),
      error: (error) => (this.result.message = error.message),
    });
  }
}
