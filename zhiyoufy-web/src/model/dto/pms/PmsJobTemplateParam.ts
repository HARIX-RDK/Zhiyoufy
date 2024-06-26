export interface PmsJobTemplateParam {
  projectId: number;
  projectName: string;

  folderId: number;
  folderName: string;

  name: string;
  description: string;
  jobPath: string;
  workerLabels: string;
  timeoutSeconds: number;
  baseConfPath: string;
  privateConfPath: string;
  configSingles: string;
  configCollections: string;
  extraArgs: string;
  isPerf: boolean;
  dashboardAddr: string;
}
