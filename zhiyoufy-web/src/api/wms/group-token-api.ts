import type { CommonResult, CommonPage, DeleteInfo, UpdateInfo } from "@/model/dto/common";

import type { WmsGroupToken, WmsGroupTokenParam, WmsGroupTokenQueryParam, WmsGroupTokenUpdateParam } from "@/model/dto/wms";


export interface GroupTokenApi {
  addGroupToken(data: WmsGroupTokenParam): Promise<CommonResult<WmsGroupToken>>;
  delGroupToken(groupTokenId: number): Promise<CommonResult<DeleteInfo>>;
  getGroupTokenList(params: WmsGroupTokenQueryParam): Promise<CommonResult<CommonPage<WmsGroupToken>>>;
  updateGroupToken({groupTokenId, data}: {groupTokenId: number, data: WmsGroupTokenUpdateParam}): Promise<CommonResult<UpdateInfo>>;
}
