import type { PageQueryParam } from "../common";


export interface WmsWorkerGroupQueryParam extends PageQueryParam {
  workerAppId: number;
  exactMatch?: boolean;
  keyword?: string;
  sortBy?: string;
  sortDesc?: boolean;
}
