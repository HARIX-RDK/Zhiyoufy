export interface WmsWorkerAppUserRelation {
  id: number;
  workerAppId: number;
  userId: number;
  isOwner: boolean;
  isEditor: boolean;
}
