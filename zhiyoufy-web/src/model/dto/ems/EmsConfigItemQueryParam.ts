import type { PageQueryParam } from "../common";


export interface EmsConfigItemQueryParam extends PageQueryParam {
  collectionId: number;
  exactMatch?: boolean;
  keyword?: string;
  sortBy?: string;
  sortDesc?: boolean;
}
