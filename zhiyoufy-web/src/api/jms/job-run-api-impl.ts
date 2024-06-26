import type { JobRunApi } from "./job-run-api";
import type { CommonResult, CommonPage } from "@/model/dto/common";

import type { JmsStartJobRunParam, JmsActiveJobRunBase, JmsJobRunResultQueryParam, JmsJobRunResultFull,
  JmsJobRunUpdatePerfParallelNumReq,
  JmsJobChildRunResultQueryParam, JmsJobChildRunResultFull } from "@/model/dto/jms";

import { axiosInst } from "@/utils/request";
import { defaultSettings } from "@/settings";

const moduleApiPrefix = `${defaultSettings.apiPrefix}/job-run`;


export class JobRunApiImpl implements JobRunApi {
  startJobRun(data: JmsStartJobRunParam): Promise<CommonResult<never>> {
    return axiosInst({
      url: `${moduleApiPrefix}/start`,
      method: 'post',
      data
    });
  }

  stopJobRun(runGuid: string): Promise<CommonResult<never>> {
    return axiosInst({
      url: `${moduleApiPrefix}/stop/${runGuid}`,
      method: 'post',
    });
  }

  updatePerfParallelNum(data: JmsJobRunUpdatePerfParallelNumReq): Promise<CommonResult<never>> {
    return axiosInst({
      url: `${moduleApiPrefix}/updatePerfParallelNum`,
      method: 'post',
      data
    });
  }

  getActiveJobRunBaseList(allUsers: boolean): Promise<CommonResult<Array<JmsActiveJobRunBase>>> {
    const params: any = {};
    allUsers && (params["allUsers"] = allUsers);

    return axiosInst({
      url: `${moduleApiPrefix}/active-base-list`,
      method: 'get',
      params,
    })
  }

  getActiveJobRunBaseSingle(runGuid: string): Promise<CommonResult<JmsActiveJobRunBase>> {
    return axiosInst({
      url: `${moduleApiPrefix}/active-base/${runGuid}`,
      method: 'get',
    });
  }

  getJobRunResultList(params: JmsJobRunResultQueryParam): Promise<CommonResult<CommonPage<JmsJobRunResultFull>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/result-list`,
      method: 'get',
      params,
    })
  }

  getJobChildRunResultList(params: JmsJobChildRunResultQueryParam): Promise<CommonResult<CommonPage<JmsJobChildRunResultFull>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/child-result-list`,
      method: 'get',
      params,
    })
  }
}
