import type { PageQueryParam } from "../common";


export interface JmsJobRunResultQueryParam extends PageQueryParam {
  environmentId?: number;
  projectId: number;
  templateName?: string
  runTag?: string;
}
