import type { JobScheduleApi } from "./job-schedule-api";
import type { CommonResult, CommonPage, DeleteInfo, UpdateInfo } from "@/model/dto/common";

import type { JmsJobScheduleParam, JmsJobSchedule, JmsJobScheduleQueryParam, JmsJobScheduleUpdateParam } from "@/model/dto/jms";

import { axiosInst } from "@/utils/request";
import { defaultSettings } from "@/settings";

const moduleApiPrefix = `${defaultSettings.apiPrefix}/job-schedule`;


export class JobScheduleApiImpl implements JobScheduleApi {
  addJobSchedule(data: JmsJobScheduleParam): Promise<CommonResult<JmsJobSchedule>> {
    return axiosInst({
      url: `${moduleApiPrefix}/add-job-schedule`,
      method: 'post',
      data
    });
  }

  delJobSchedule(jobScheduleId: number): Promise<CommonResult<DeleteInfo>> {
    return axiosInst({
      url: `${moduleApiPrefix}/del-job-schedule/${jobScheduleId}`,
      method: 'delete',
    });
  }

  getJobScheduleList(params: JmsJobScheduleQueryParam): Promise<CommonResult<CommonPage<JmsJobSchedule>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/job-schedule-list`,
      method: 'get',
      params,
    })
  }

  updateJobSchedule({jobScheduleId, data}: {jobScheduleId: number, data: JmsJobScheduleUpdateParam}): Promise<CommonResult<UpdateInfo>> {
    return axiosInst({
      url: `${moduleApiPrefix}/update-job-schedule/${jobScheduleId}`,
      method: 'post',
      data
    });
  }
}
