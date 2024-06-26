import type { WorkerGroupApi } from "./worker-group-api";

import type { CommonResult, CommonPage, DeleteInfo, UpdateInfo } from "@/model/dto/common";

import type { WmsWorkerGroup, WmsWorkerGroupParam, WmsWorkerGroupQueryParam, WmsWorkerGroupUpdateParam } from "@/model/dto/wms";

import { axiosInst } from "@/utils/request";
import { defaultSettings } from "@/settings";

const moduleApiPrefix = `${defaultSettings.apiPrefix}/worker-group`;


export class WorkerGroupApiImpl implements WorkerGroupApi {
  addWorkerGroup(data: WmsWorkerGroupParam): Promise<CommonResult<WmsWorkerGroup>> {
    return axiosInst({
      url: `${moduleApiPrefix}/add-worker-group`,
      method: 'post',
      data
    });
  }

  delWorkerGroup(workerGroupId: number): Promise<CommonResult<DeleteInfo>> {
    return axiosInst({
      url: `${moduleApiPrefix}/del-worker-group/${workerGroupId}`,
      method: 'delete',
    });
  }

  getWorkerGroupList(params: WmsWorkerGroupQueryParam): Promise<CommonResult<CommonPage<WmsWorkerGroup>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/worker-group-list`,
      method: 'get',
      params,
    })
  }

  updateWorkerGroup({workerGroupId, data}: {workerGroupId: number, data: WmsWorkerGroupUpdateParam}): Promise<CommonResult<UpdateInfo>> {
    return axiosInst({
      url: `${moduleApiPrefix}/update-worker-group/${workerGroupId}`,
      method: 'post',
      data
    });
  }
}
