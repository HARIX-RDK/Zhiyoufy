export interface JmsJobScheduleParam {
  name: string;
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

  includeTags: string;
  excludeTags: string;
  addTags: string;
  removeTags: string;

  crontabConfig: string;
}
