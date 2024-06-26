import type { PageQueryParam } from "../common";


export interface WmsGroupTokenQueryParam extends PageQueryParam {
  workerGroupId: number;
  exactMatch?: boolean;
  keyword?: string;
  sortBy?: string;
  sortDesc?: boolean;
}
