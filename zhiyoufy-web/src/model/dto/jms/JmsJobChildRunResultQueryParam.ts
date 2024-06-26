import type { PageQueryParam } from "../common";


export interface JmsJobChildRunResultQueryParam extends PageQueryParam {
  environmentId?: number;
  projectId?: number;
  templateId?: number;
  runGuid: string;
}
