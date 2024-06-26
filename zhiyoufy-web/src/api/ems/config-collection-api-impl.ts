import type { ConfigCollectionApi } from "./config-collection-api";

import type { CommonResult, CommonPage } from "@/model/dto/common";

import type { EmsConfigCollection, EmsConfigCollectionParam,
  EmsConfigCollectionQueryParam, EmsConfigCollectionUpdateParam } from "@/model/dto/ems";

import { axiosInst } from "@/utils/request";
import { defaultSettings } from "@/settings";

const moduleApiPrefix = `${defaultSettings.apiPrefix}/config-collection`;


export class ConfigCollectionApiImpl implements ConfigCollectionApi {
  addConfigCollection(data: EmsConfigCollectionParam): Promise<CommonResult<EmsConfigCollection>> {
    return axiosInst({
      url: `${moduleApiPrefix}/add-config-collection`,
      method: 'post',
      data
    });
  }

  delConfigCollection(configCollectionId: number): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/del-config-collection/${configCollectionId}`,
      method: 'delete',
    });
  }

  getConfigCollectionList(params: EmsConfigCollectionQueryParam): Promise<CommonResult<CommonPage<EmsConfigCollection>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/config-collection-list`,
      method: 'get',
      params,
    });
  }

  updateConfigCollection({configCollectionId, data}: {configCollectionId: number, data: EmsConfigCollectionUpdateParam}): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/update-config-collection/${configCollectionId}`,
      method: 'post',
      data
    });
  }
}
