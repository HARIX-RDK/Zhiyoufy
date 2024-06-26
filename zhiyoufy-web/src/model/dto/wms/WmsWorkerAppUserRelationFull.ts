
import type { WmsWorkerAppUserRelation } from "./WmsWorkerAppUserRelation";


export interface WmsWorkerAppUserRelationFull extends WmsWorkerAppUserRelation {
  username: string;
}

export type WmsWorkerAppUserRelationParam = Omit<WmsWorkerAppUserRelationFull, "id">;
