import type { CommonResult, CommonPage, DeleteInfo, UpdateInfo } from "@/model/dto/common";

import type { JmsJobScheduleParam, JmsJobSchedule, JmsJobScheduleQueryParam, JmsJobScheduleUpdateParam } from "@/model/dto/jms";


export interface JobScheduleApi {
  addJobSchedule(data: JmsJobScheduleParam): Promise<CommonResult<JmsJobSchedule>>;
  delJobSchedule(jobScheduleId: number): Promise<CommonResult<DeleteInfo>>;
  getJobScheduleList(params: JmsJobScheduleQueryParam): Promise<CommonResult<CommonPage<JmsJobSchedule>>>;
  updateJobSchedule({jobScheduleId, data}: {jobScheduleId: number, data: JmsJobScheduleUpdateParam}): Promise<CommonResult<UpdateInfo>>;
}
