import type { ConfigItemApi } from "./config-item-api";

import type { CommonResult, CommonPage } from "@/model/dto/common";

import type { EmsConfigItem, EmsConfigItemParam,
  EmsConfigItemQueryParam, EmsConfigItemUpdateParam } from "@/model/dto/ems";

import { axiosInst } from "@/utils/request";
import { defaultSettings } from "@/settings";

const moduleApiPrefix = `${defaultSettings.apiPrefix}/config-item`;


export class ConfigItemApiImpl implements ConfigItemApi {
  addConfigItem(data: EmsConfigItemParam): Promise<CommonResult<EmsConfigItem>> {
    return axiosInst({
      url: `${moduleApiPrefix}/add-config-item`,
      method: 'post',
      data
    });
  }

  delConfigItem(configItemId: number): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/del-config-item/${configItemId}`,
      method: 'delete',
    });
  }

  getConfigItemList(params: EmsConfigItemQueryParam): Promise<CommonResult<CommonPage<EmsConfigItem>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/config-item-list`,
      method: 'get',
      params,
    });
  }

  updateConfigItem({configItemId, data}: {configItemId: number, data: EmsConfigItemUpdateParam}): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/update-config-item/${configItemId}`,
      method: 'post',
      data
    });
  }
}
