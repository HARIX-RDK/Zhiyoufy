import type { PageQueryParam } from "../common";


export interface WmsWorkerAppQueryParam extends PageQueryParam {
  allUsers?: boolean;
  exactMatch?: boolean;
  keyword?: string;
  sortBy?: string;
  sortDesc?: boolean;
}
