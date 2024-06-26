import type { CommonResult } from "@/model/dto/common";

import type { WmsActiveWorkerBase, WmsActiveWorkerGroupBase, WmsWorkerAppBase } from "@/model/dto/wms";


export interface ActiveWorkerApi {
  getAppBaseList(): Promise<CommonResult<Array<WmsWorkerAppBase>>>;
  getGroupBaseList(workerAppId: number): Promise<CommonResult<Array<WmsActiveWorkerGroupBase>>>;
  getWorkerBaseList(workerGroupId: number): Promise<CommonResult<Array<WmsActiveWorkerBase>>>;
  disconnectSession(sessionId: string): Promise<CommonResult<never>>;
}
