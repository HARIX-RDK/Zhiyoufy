import type { PageQueryParam } from "../common";


export interface EmsConfigCollectionQueryParam extends PageQueryParam {
  envId: number;
  exactMatch?: boolean;
  keyword?: string;
  sortBy?: string;
  sortDesc?: boolean;
}
