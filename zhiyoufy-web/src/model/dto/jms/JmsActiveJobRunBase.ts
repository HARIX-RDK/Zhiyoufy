export interface JmsActiveJobRunBase {
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
  activeNum: number;
  finishedNum: number;
  passedNum: number;
  createdOn: string;
  state: string;

  perf: boolean;
  dashboardAddr: string;
  perfParallelNum: number;
  workerNames: string[];
}
