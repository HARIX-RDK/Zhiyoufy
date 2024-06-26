import type { PageQueryParam } from "../common";


export interface EmsEnvironmentQueryParam extends PageQueryParam {
  allUsers?: boolean;
  exactMatch?: boolean;
  keyword?: string;
  sortBy?: string;
  sortDesc?: boolean;
}
