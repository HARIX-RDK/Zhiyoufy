import type { PageQueryParam } from "../common";


export interface UmsRoleQueryParam extends PageQueryParam {
  keyword?: string;
  sortBy?: string;
  sortDesc?: boolean;
}
