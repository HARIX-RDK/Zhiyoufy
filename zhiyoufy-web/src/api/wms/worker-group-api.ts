import type { CommonResult, CommonPage, DeleteInfo, UpdateInfo } from "@/model/dto/common";

import type { WmsWorkerGroup, WmsWorkerGroupParam, WmsWorkerGroupQueryParam, WmsWorkerGroupUpdateParam } from "@/model/dto/wms";


export interface WorkerGroupApi {
  addWorkerGroup(data: WmsWorkerGroupParam): Promise<CommonResult<WmsWorkerGroup>>;
  delWorkerGroup(workerGroupId: number): Promise<CommonResult<DeleteInfo>>;
  getWorkerGroupList(params: WmsWorkerGroupQueryParam): Promise<CommonResult<CommonPage<WmsWorkerGroup>>>;
  updateWorkerGroup({workerGroupId, data}: {workerGroupId: number, data: WmsWorkerGroupUpdateParam}): Promise<CommonResult<UpdateInfo>>;
}
