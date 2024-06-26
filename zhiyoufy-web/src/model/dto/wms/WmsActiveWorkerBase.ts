export interface WmsActiveWorkerBase {
  disconnected: boolean;
  connectTime: string;
  disconnectTime: string;
  sessionId: string;
  appRunId: string;
  appStartTimestamp: string;
  workerName: string;
  workerLabels: Record<string, string>;
  maxActiveJobNum: number;
  freeActiveJobNum: number;
}
