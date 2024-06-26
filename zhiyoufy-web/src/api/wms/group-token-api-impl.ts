import type { GroupTokenApi } from "./group-token-api";

import type { CommonResult, CommonPage, DeleteInfo, UpdateInfo } from "@/model/dto/common";

import type { WmsGroupToken, WmsGroupTokenParam, WmsGroupTokenQueryParam, WmsGroupTokenUpdateParam } from "@/model/dto/wms";

import { axiosInst } from "@/utils/request";
import { defaultSettings } from "@/settings";

const moduleApiPrefix = `${defaultSettings.apiPrefix}/group-token`;


export class GroupTokenApiImpl implements GroupTokenApi {
  addGroupToken(data: WmsGroupTokenParam): Promise<CommonResult<WmsGroupToken>> {
    return axiosInst({
      url: `${moduleApiPrefix}/add-group-token`,
      method: 'post',
      data
    });
  }

  delGroupToken(groupTokenId: number): Promise<CommonResult<DeleteInfo>> {
    return axiosInst({
      url: `${moduleApiPrefix}/del-group-token/${groupTokenId}`,
      method: 'delete',
    });
  }

  getGroupTokenList(params: WmsGroupTokenQueryParam): Promise<CommonResult<CommonPage<WmsGroupToken>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/group-token-list`,
      method: 'get',
      params,
    })
  }

  updateGroupToken({groupTokenId, data}: {groupTokenId: number, data: WmsGroupTokenUpdateParam}): Promise<CommonResult<UpdateInfo>> {
    return axiosInst({
      url: `${moduleApiPrefix}/update-group-token/${groupTokenId}`,
      method: 'post',
      data
    });
  }
}
