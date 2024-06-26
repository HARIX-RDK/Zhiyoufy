import type { ConfigSingleApi } from "./config-single-api";

import type { CommonResult, CommonPage } from "@/model/dto/common";

import type { EmsConfigSingle, EmsConfigSingleParam,
  EmsConfigSingleQueryParam, EmsConfigSingleUpdateParam } from "@/model/dto/ems";

import { axiosInst } from "@/utils/request";
import { defaultSettings } from "@/settings";

const moduleApiPrefix = `${defaultSettings.apiPrefix}/config-single`;


export class ConfigSingleApiImpl implements ConfigSingleApi {
  addConfigSingle(data: EmsConfigSingleParam): Promise<CommonResult<EmsConfigSingle>> {
    return axiosInst({
      url: `${moduleApiPrefix}/add-config-single`,
      method: 'post',
      data
    });
  }

  delConfigSingle(configSingleId: number): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/del-config-single/${configSingleId}`,
      method: 'delete',
    });
  }

  getConfigSingleList(params: EmsConfigSingleQueryParam): Promise<CommonResult<CommonPage<EmsConfigSingle>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/config-single-list`,
      method: 'get',
      params,
    });
  }

  updateConfigSingle({configSingleId, data}: {configSingleId: number, data: EmsConfigSingleUpdateParam}): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/update-config-single/${configSingleId}`,
      method: 'post',
      data
    });
  }
}
