import type { PageQueryParam } from "../common";


export interface PmsProjectQueryParam extends PageQueryParam {
  allUsers?: boolean;
  exactMatch?: boolean;
  keyword?: string;
  sortBy?: string;
  sortDesc?: boolean;
}
