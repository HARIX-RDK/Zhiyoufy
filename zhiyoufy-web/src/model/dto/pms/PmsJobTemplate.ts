export interface PmsJobTemplate {
  id: number;
  projectId: number;
  folderId: number;
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
  createdTime: string;
  createdBy: string;
  modifiedTime: string;
  modifiedBy: string;
  isPerf: boolean;
  dashboardAddr: string;
}
