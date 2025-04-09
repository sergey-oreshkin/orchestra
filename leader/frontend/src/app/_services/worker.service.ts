import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { Worker } from '../_models';
import { Strategy } from '../_models/strategy';
import { Param } from '../_models/param';
import { Launch } from '../_models/launch';

@Injectable({
  providedIn: 'root',
})
export class WorkerService {
  constructor(private httpClient: HttpClient) {}

  getWorkers(): Observable<Worker[]> {
    return this.httpClient.get<Worker[]>('/status/worker');
  }

  getStrategies(): Observable<Strategy[]> {
    return this.httpClient.get<Strategy[]>('/status/strategy');
  }

  getParams(): Observable<Param[]> {
    return this.httpClient.get<Param[]>('/params');
  }

  launch(launch: Launch): Observable<Launch> {
    return this.httpClient
      .post<Launch>('/launch', launch)
      .pipe(
        catchError((error: HttpErrorResponse) =>
          throwError(() => new Error('Статус ' + error.status))
        )
      );
  }
}
