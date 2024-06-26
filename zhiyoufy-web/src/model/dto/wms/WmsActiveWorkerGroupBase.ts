export interface WmsActiveWorkerGroupBase {
  id: number;
  workerAppId: number;
  name: string;
  maxActiveJobNum: number;
  freeActiveJobNum: number;
  workerLabels: string;
}
