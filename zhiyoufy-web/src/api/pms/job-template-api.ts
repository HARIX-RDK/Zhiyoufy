import type { CommonResult, CommonPage, DeleteInfo, UpdateInfo } from "@/model/dto/common";

import type { PmsJobTemplate, PmsJobTemplateParam,
  PmsJobTemplateQueryParam, PmsJobTemplateUpdateParam } from "@/model/dto/pms";


export interface JobTemplateApi {
  addJobTemplate(data: PmsJobTemplateParam): Promise<CommonResult<PmsJobTemplate>>;
  delJobTemplate(jobTemplateId: number): Promise<CommonResult<DeleteInfo>>;
  getJobTemplateList(params: PmsJobTemplateQueryParam): Promise<CommonResult<CommonPage<PmsJobTemplate>>>;
  updateJobTemplate({jobTemplateId, data}: {jobTemplateId: number, data: PmsJobTemplateUpdateParam}): Promise<CommonResult<UpdateInfo>>;
}
