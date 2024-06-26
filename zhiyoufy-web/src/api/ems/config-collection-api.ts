import type { CommonResult, CommonPage } from "@/model/dto/common";

import type { EmsConfigCollection, EmsConfigCollectionParam,
  EmsConfigCollectionQueryParam, EmsConfigCollectionUpdateParam } from "@/model/dto/ems";


export interface ConfigCollectionApi {
  addConfigCollection(data: EmsConfigCollectionParam): Promise<CommonResult<EmsConfigCollection>>;
  delConfigCollection(configCollectionId: number): Promise<CommonResult<number>>;
  getConfigCollectionList(params: EmsConfigCollectionQueryParam): Promise<CommonResult<CommonPage<EmsConfigCollection>>>;
  updateConfigCollection({configCollectionId, data}: {configCollectionId: number, data: EmsConfigCollectionUpdateParam}): Promise<CommonResult<number>>;
}
