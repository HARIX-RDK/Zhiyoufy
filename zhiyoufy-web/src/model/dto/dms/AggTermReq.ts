export interface AggTermReq {
  guid: string;
  startTime: string;
  endTime: string;
  targetTerm: string;
  projectName: string;
  environmentName: string;
  templateName: string;
  runTag: string;
  termSize: number;
}
