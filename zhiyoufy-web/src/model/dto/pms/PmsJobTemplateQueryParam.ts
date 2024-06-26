import type { PageQueryParam } from "../common";


export interface PmsJobTemplateQueryParam extends PageQueryParam {
  projectId: number;
  folderId?: number;
  exactMatch?: boolean;
  keyword?: string;
  sortBy?: string;
  sortDesc?: boolean;
}
