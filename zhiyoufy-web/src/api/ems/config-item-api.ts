import type { CommonResult, CommonPage } from "@/model/dto/common";

import type { EmsConfigItem, EmsConfigItemParam,
  EmsConfigItemQueryParam, EmsConfigItemUpdateParam } from "@/model/dto/ems";


export interface ConfigItemApi {
  addConfigItem(data: EmsConfigItemParam): Promise<CommonResult<EmsConfigItem>>;
  delConfigItem(configItemId: number): Promise<CommonResult<number>>;
  getConfigItemList(params: EmsConfigItemQueryParam): Promise<CommonResult<CommonPage<EmsConfigItem>>>;
  updateConfigItem({configItemId, data}: {configItemId: number, data: EmsConfigItemUpdateParam}): Promise<CommonResult<number>>;
}
