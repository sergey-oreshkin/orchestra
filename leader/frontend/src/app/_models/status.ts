import { Strategy } from './strategy';
import { Worker } from './worker';

export interface Status {
  workers: Worker;
  strategies: Strategy[];
}
