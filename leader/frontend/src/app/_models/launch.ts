export interface Launch {
  number: number;
  strategy: string | null;
  start: boolean | null;
  params: {} | null;
  message: string | null;
  success: boolean | null;
}
