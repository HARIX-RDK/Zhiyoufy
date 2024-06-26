export interface WmsGroupTokenParam {
  workerAppId: number;
  workerAppName: string;
  workerGroupId: number;
  workerGroupName: string;
  name: string;
  secret: string;
  expiryTime?: string;
  description: string;
}
