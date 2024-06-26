export interface JmsStartJobRunParam {
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

  includeTags: string;
  excludeTags: string;
  addTags: string;
  removeTags: string;
}
