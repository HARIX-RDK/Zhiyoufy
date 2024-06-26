import type { PmsProjectUserRelation } from "./PmsProjectUserRelation";


export interface PmsProjectUserRelationFull extends PmsProjectUserRelation {
  username: string;
}

export type PmsProjectUserRelationParam = Omit<PmsProjectUserRelationFull, 'id'>;
