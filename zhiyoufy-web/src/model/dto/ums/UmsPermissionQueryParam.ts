import type { PageQueryParam } from "../common";


export interface UmsPermissionQueryParam extends PageQueryParam {
  keyword?: string;
  sortBy?: string;
  sortDesc?: boolean;
}
