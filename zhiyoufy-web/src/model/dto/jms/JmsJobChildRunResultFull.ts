export interface JmsJobChildRunResultFull {
  id: string;
  timestamp: string;
  environmentId: number;
  environmentName: string;
  projectId: number;
  projectName: string;
  templateId: number;
  templateName: string;
  messageId: string;
  runGuid: string;
  index: number;
  endReason: string;
  endDetail: string;
  endOk: boolean;
  resultOk: boolean;
  passed: boolean;
  jobOutputUrl: string;
  childStatusCntMap: any;
}
