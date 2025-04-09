import { Component, OnInit } from '@angular/core';
import { Param } from '../_models/param';
import { WorkerService } from '../_services';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { NgClass, NgFor } from '@angular/common';
import { Launch } from '../_models/launch';

@Component({
  selector: 'app-strategy-form',
  imports: [ReactiveFormsModule, NgFor, NgClass],
  templateUrl: './strategy-form.component.html',
  styleUrl: './strategy-form.component.scss',
})
export class StrategyFormComponent implements OnInit {
  params: Param[] = [];
  current: string[] = [];
  result: Launch = {
    number: -1,
    strategy: '',
    start: null,
    params: null,
    message: '',
    success: null,
  };
  form: FormGroup = new FormGroup({ name: new FormControl(null) });

  constructor(private workerService: WorkerService) {}

  ngOnInit(): void {
    this.workerService.getParams().subscribe((res) => {
      this.params = res;
    });
  }

  launch() {
    const p: any = {};
    this.current.forEach((c) => {
      p[c] = this.form.get(c)?.value;
    });
    const launch: Launch = {
      number: -1,
      strategy: this.form.get('name')?.value,
      start: true,
      params: p,
      message: '',
      success: null,
    };
    this.workerService.launch(launch).subscribe({
      next: (res) => (this.result = res),
      error: (error) => (this.result.message = error.message),
    });
  }

  change() {
    const param = this.params.filter(
      (p) => p.name === this.form.controls['name'].value
    )[0];
    this.makeForm(param);
    this.current = param.params;
  }

  makeForm(param: Param) {
    const controls: any = {};
    controls['name'] = new FormControl(param.name);
    param.params.forEach((p) => (controls[p] = new FormControl('')));
    this.form = new FormGroup(controls);
  }
}
