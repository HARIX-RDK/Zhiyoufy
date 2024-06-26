import type { EmsEnvironmentUserRelation } from './EmsEnvironmentUserRelation';


export interface EmsEnvironmentUserRelationFull extends EmsEnvironmentUserRelation {
  username: string;
}

export type EmsEnvironmentUserRelationParam = Omit<EmsEnvironmentUserRelationFull, 'id'>;
