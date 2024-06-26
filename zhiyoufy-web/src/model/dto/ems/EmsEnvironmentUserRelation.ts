export interface EmsEnvironmentUserRelation {
  id: number;
  environmentId: number;
  userId: number;
  isOwner: boolean;
  isEditor: boolean;
}
