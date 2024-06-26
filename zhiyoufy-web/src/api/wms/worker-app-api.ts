import type { CommonResult, CommonPage, PageQueryParam, DeleteInfo, UpdateInfo } from "@/model/dto/common";

import type { WmsWorkerApp, WmsWorkerAppParam, WmsWorkerAppBase,
  WmsWorkerAppQueryParam, WmsWorkerAppFull, WmsWorkerAppUpdateParam,
  WmsWorkerAppUserRelationFull, WmsWorkerAppUserRelationParam, WmsWorkerAppUserRelationUpdateParam } from "@/model/dto/wms";


export interface WorkerAppApi {
  addWorkerApp(data: WmsWorkerAppParam): Promise<CommonResult<WmsWorkerApp>>;
  delWorkerApp(workerAppId: number): Promise<CommonResult<DeleteInfo>>;
  getWorkerAppBaseList(): Promise<CommonResult<Array<WmsWorkerAppBase>>>;
  getWorkerAppList(params: WmsWorkerAppQueryParam): Promise<CommonResult<CommonPage<WmsWorkerAppFull>>>;
  updateWorkerApp({workerAppId, data}: {workerAppId: number, data: WmsWorkerAppUpdateParam}): Promise<CommonResult<UpdateInfo>>;

  addWorkerAppUser(data: WmsWorkerAppUserRelationParam): Promise<CommonResult<number>>;
  delWorkerAppUser(relationId: number): Promise<CommonResult<number>>;
  getWorkerAppUserList({workerAppId, params}: {workerAppId: number, params: PageQueryParam}): Promise<CommonResult<CommonPage<WmsWorkerAppUserRelationFull>>>;
  updateWorkerAppUser({relationId, data}: {relationId: number, data: WmsWorkerAppUserRelationUpdateParam}): Promise<CommonResult<number>>;
}
