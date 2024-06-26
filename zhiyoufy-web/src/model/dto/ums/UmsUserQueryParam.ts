import type { PageQueryParam } from "../common";

export interface UmsUserQueryParam extends PageQueryParam {
  admin?: boolean;
  sysAdmin?: boolean;
  keyword?: string;
  sortBy?: string;
  sortDesc?: boolean;
}
