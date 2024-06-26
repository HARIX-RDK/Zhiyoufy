import type { CommonResult, CommonPage } from "@/model/dto/common";

import type { JmsStartJobRunParam, JmsActiveJobRunBase, JmsJobRunResultQueryParam, JmsJobRunResultFull,
  JmsJobRunUpdatePerfParallelNumReq,
  JmsJobChildRunResultQueryParam, JmsJobChildRunResultFull } from "@/model/dto/jms";


export interface JobRunApi {
  startJobRun(data: JmsStartJobRunParam): Promise<CommonResult<never>>;
  stopJobRun(runGuid: string): Promise<CommonResult<never>>;
  updatePerfParallelNum(data: JmsJobRunUpdatePerfParallelNumReq): Promise<CommonResult<never>>;
  getActiveJobRunBaseList(allUsers: boolean): Promise<CommonResult<Array<JmsActiveJobRunBase>>>;
  getActiveJobRunBaseSingle(runGuid: string): Promise<CommonResult<JmsActiveJobRunBase>>;
  getJobRunResultList(params: JmsJobRunResultQueryParam): Promise<CommonResult<CommonPage<JmsJobRunResultFull>>>;
  getJobChildRunResultList(params: JmsJobChildRunResultQueryParam): Promise<CommonResult<CommonPage<JmsJobChildRunResultFull>>>;
}
