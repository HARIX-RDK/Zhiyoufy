export interface WmsGroupToken {
  id: number;
  workerAppId: number;
  workerGroupId: number;
  name: string;
  secret: string;
  expiryTime: string;
  description: string;
  createdTime: string;
  createdBy: string;
  modifiedTime: string;
  modifiedBy: string;
}
