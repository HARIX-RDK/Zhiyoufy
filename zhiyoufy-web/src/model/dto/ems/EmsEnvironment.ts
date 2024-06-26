export interface EmsEnvironment {
  id: number;
  parentId?: number;
  name: string;
  description: string;
  workerLabels: string;
  extraArgs: string;
  createdTime: string;
  createdBy: string;
  modifiedTime: string;
  modifiedBy: string;
}
