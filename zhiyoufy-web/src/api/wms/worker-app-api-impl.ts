import type { WorkerAppApi } from "./worker-app-api";
import type { CommonResult, CommonPage, PageQueryParam, DeleteInfo, UpdateInfo } from "@/model/dto/common";

import type { WmsWorkerApp, WmsWorkerAppParam, WmsWorkerAppBase,
  WmsWorkerAppQueryParam, WmsWorkerAppFull, WmsWorkerAppUpdateParam,
  WmsWorkerAppUserRelationFull, WmsWorkerAppUserRelationParam, WmsWorkerAppUserRelationUpdateParam } from "@/model/dto/wms";

import { axiosInst } from "@/utils/request";
import { defaultSettings } from "@/settings";

const moduleApiPrefix = `${defaultSettings.apiPrefix}/worker-app`;


export class WorkerAppApiImpl implements WorkerAppApi {
  addWorkerApp(data: WmsWorkerAppParam): Promise<CommonResult<WmsWorkerApp>> {
    return axiosInst({
      url: `${moduleApiPrefix}/add-worker-app`,
      method: 'post',
      data
    });
  }

  delWorkerApp(workerAppId: number): Promise<CommonResult<DeleteInfo>> {
    return axiosInst({
      url: `${moduleApiPrefix}/del-worker-app/${workerAppId}`,
      method: 'delete',
    });
  }

  getWorkerAppBaseList(): Promise<CommonResult<Array<WmsWorkerAppBase>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/worker-app-base-list`,
      method: 'get',
    })
  }

  getWorkerAppList(params: WmsWorkerAppQueryParam): Promise<CommonResult<CommonPage<WmsWorkerAppFull>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/worker-app-list`,
      method: 'get',
      params,
    })
  }

  updateWorkerApp({workerAppId, data}: {workerAppId: number, data: WmsWorkerAppUpdateParam}): Promise<CommonResult<UpdateInfo>> {
    return axiosInst({
      url: `${moduleApiPrefix}/update-worker-app/${workerAppId}`,
      method: 'post',
      data
    });
  }

  addWorkerAppUser(data: WmsWorkerAppUserRelationParam): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/add-worker-app-user`,
      method: 'post',
      data
    });
  }

  delWorkerAppUser(relationId: number): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/del-worker-app-user/${relationId}`,
      method: 'delete',
    });
  }

  getWorkerAppUserList({workerAppId, params}: {workerAppId: number, params: PageQueryParam}): Promise<CommonResult<CommonPage<WmsWorkerAppUserRelationFull>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/worker-app-user-list/${workerAppId}`,
      method: 'get',
      params,
    })
  }

  updateWorkerAppUser({relationId, data}: {relationId: number, data: WmsWorkerAppUserRelationUpdateParam}): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/update-worker-app-user/${relationId}`,
      method: 'post',
      data
    });
  }
}
