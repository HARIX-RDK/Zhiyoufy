import type { PageQueryParam } from "../common";


export interface PmsJobFolderQueryParam extends PageQueryParam {
  projectId: number;
  parentId?: number;
  exactMatch?: boolean;
  keyword?: string;
  sortBy?: string;
  sortDesc?: boolean;
}
