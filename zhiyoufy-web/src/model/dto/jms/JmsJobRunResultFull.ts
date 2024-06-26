export interface JmsJobRunResultFull {
  runGuid: string;
  workerAppId: number;
  workerAppName: string;
  workerGroupId: number;
  workerGroupName: string;
  environmentId: number;
  environmentName: string;
  projectId: number;
  projectName: string;
  templateId: number;
  templateName: string;
  runTag: string;
  runNum: number;
  parallelNum: number;
  username: string;

  startedNum: number;
  finishedNum: number;
  passedNum: number;
  createdOn: string;
  timestamp: string;
  durationSeconds: number;
  passed: boolean;
  endReason: string;
}
