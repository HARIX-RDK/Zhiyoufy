export interface PmsJobTemplateUpdateParam {
  name?: string;
  description?: string;
  jobPath?: string;
  workerLabels?: string;
  timeoutSeconds?: number;
  baseConfPath?: string;
  privateConfPath?: string;
  configSingles?: string;
  configCollections?: string;
  extraArgs?: string;
  isPerf?: boolean;
  dashboardAddr?: string;
}
