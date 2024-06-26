import type { CommonResult, CommonPage } from "@/model/dto/common";

import type { EmsConfigSingle, EmsConfigSingleParam,
  EmsConfigSingleQueryParam, EmsConfigSingleUpdateParam } from "@/model/dto/ems";


export interface ConfigSingleApi {
  addConfigSingle(data: EmsConfigSingleParam): Promise<CommonResult<EmsConfigSingle>>;
  delConfigSingle(configSingleId: number): Promise<CommonResult<number>>;
  getConfigSingleList(params: EmsConfigSingleQueryParam): Promise<CommonResult<CommonPage<EmsConfigSingle>>>;
  updateConfigSingle({configSingleId, data}: {configSingleId: number, data: EmsConfigSingleUpdateParam}): Promise<CommonResult<number>>;
}
