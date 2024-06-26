export interface PmsProjectUserRelation {
  id: number;
  projectId: number;
  userId: number;
  isOwner: boolean;
  isEditor: boolean;
}
