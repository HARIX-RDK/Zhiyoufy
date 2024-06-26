import type { PageQueryParam } from "../common";


export interface EmsConfigSingleQueryParam extends PageQueryParam {
  envId: number;
  exactMatch?: boolean;
  keyword?: string;
  sortBy?: string;
  sortDesc?: boolean;
}
