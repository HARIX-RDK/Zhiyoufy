import type { JobTemplateApi } from "./job-template-api";

import type { CommonResult, CommonPage, DeleteInfo, UpdateInfo } from "@/model/dto/common";

import type { PmsJobTemplate, PmsJobTemplateParam, PmsJobTemplateQueryParam, PmsJobTemplateUpdateParam } from "@/model/dto/pms";

import { axiosInst } from "@/utils/request";
import { defaultSettings } from "@/settings";

const moduleApiPrefix = `${defaultSettings.apiPrefix}/job-template`;


export class JobTemplateApiImpl implements JobTemplateApi {
  addJobTemplate(data: PmsJobTemplateParam): Promise<CommonResult<PmsJobTemplate>> {
    return axiosInst({
      url: `${moduleApiPrefix}/add-job-template`,
      method: 'post',
      data
    });
  }

  delJobTemplate(jobTemplateId: number): Promise<CommonResult<DeleteInfo>> {
    return axiosInst({
      url: `${moduleApiPrefix}/del-job-template/${jobTemplateId}`,
      method: 'delete',
    });
  }

  getJobTemplateList(params: PmsJobTemplateQueryParam): Promise<CommonResult<CommonPage<PmsJobTemplate>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/job-template-list`,
      method: 'get',
      params,
    })
  }

  updateJobTemplate({jobTemplateId, data}: {jobTemplateId: number, data: PmsJobTemplateUpdateParam}): Promise<CommonResult<UpdateInfo>> {
    return axiosInst({
      url: `${moduleApiPrefix}/update-job-template/${jobTemplateId}`,
      method: 'post',
      data
    });
  }
}
