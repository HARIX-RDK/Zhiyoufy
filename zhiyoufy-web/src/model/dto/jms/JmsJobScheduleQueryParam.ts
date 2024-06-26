import type { PageQueryParam } from "../common";


export interface JmsJobScheduleQueryParam extends PageQueryParam {
  environmentId?: number;
  projectId: number;
  templateName?: string;
  runTag?: string;
}
