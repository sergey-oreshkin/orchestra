import { Routes } from '@angular/router';
import { BaseTemplateComponent } from './base-template/base-template.component';

export const routes: Routes = [
  {
    path: '',
    component: BaseTemplateComponent,
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'worker',
      },
      {
        path: 'worker',
        loadComponent: () =>
          import('./worker/worker.component').then((m) => m.WorkerComponent),
      },
      {
        path: 'strategy',
        loadComponent: () =>
          import('./strategy/strategy.component').then(
            (m) => m.StrategyComponent
          ),
      },
      {
        path: 'launch',
        loadComponent: () =>
          import('./strategy-form/strategy-form.component').then(
            (m) => m.StrategyFormComponent
          ),
      },
      {
        path: 'stop',
        loadComponent: () =>
          import('./strategy-stop/strategy-stop.component').then(
            (m) => m.StrategyStopComponent
          ),
      },
    ],
  },
  {
    path: '**',
    redirectTo: '',
  },
];
