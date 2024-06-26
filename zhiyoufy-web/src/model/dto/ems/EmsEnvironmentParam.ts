export interface EmsEnvironmentParam {
  parentId?: number;
  parentName?: string;

  name: string;
  description: string;
  workerLabels: string;
  extraArgs: string;
}
