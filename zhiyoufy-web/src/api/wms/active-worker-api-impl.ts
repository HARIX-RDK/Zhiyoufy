import type { ActiveWorkerApi } from "./active-worker-api";

import type { CommonResult } from "@/model/dto/common";

import type { WmsActiveWorkerBase, WmsActiveWorkerGroupBase, WmsWorkerAppBase } from "@/model/dto/wms";

import { axiosInst } from "@/utils/request";
import { defaultSettings } from "@/settings";

const moduleApiPrefix = `${defaultSettings.apiPrefix}/active-worker`;


export class ActiveWorkerApiImpl implements ActiveWorkerApi {
  getAppBaseList(): Promise<CommonResult<Array<WmsWorkerAppBase>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/app-base-list`,
      method: 'get',
    })
  }

  getGroupBaseList(workerAppId: number): Promise<CommonResult<Array<WmsActiveWorkerGroupBase>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/group-base-list?workerAppId=${workerAppId}`,
      method: 'get',
    })
  }

  getWorkerBaseList(workerGroupId: number): Promise<CommonResult<Array<WmsActiveWorkerBase>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/worker-base-list?workerGroupId=${workerGroupId}`,
      method: 'get',
    })
  }

  disconnectSession(sessionId: string): Promise<CommonResult<never>> {
    return axiosInst({
      url: `${moduleApiPrefix}/disconnect-session/${sessionId}`,
      method: 'post',
    });
  }
}
