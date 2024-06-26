export interface AggDateHistogramReq {
  guid: string;
  startTime: string;
  endTime: string;
  interval: string;
  timeZone: string;
  format: string;
  projectName: string;
  environmentName: string;
  templateName: string;
  runTag: string;
}
