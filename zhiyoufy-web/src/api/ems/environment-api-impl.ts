import type { EnvironmentApi } from "./environment-api";
import type { CommonResult, CommonPage, PageQueryParam } from "@/model/dto/common";

import type { EmsEnvironment, EmsEnvironmentParam, EmsEnvironmentBase,
  EmsEnvironmentQueryParam, EmsEnvironmentFull, EmsEnvironmentUpdateParam,
  EmsEnvironmentUserRelationFull, EmsEnvironmentUserRelationParam, EmsEnvironmentUserRelationUpdateParam } from "@/model/dto/ems";

import { axiosInst } from "@/utils/request";
import { defaultSettings } from "@/settings";

const moduleApiPrefix = `${defaultSettings.apiPrefix}/environment`;


export class EnvironmentApiImpl implements EnvironmentApi {
  addEnvironment(data: EmsEnvironmentParam): Promise<CommonResult<EmsEnvironment>> {
    return axiosInst({
      url: `${moduleApiPrefix}/add-environment`,
      method: 'post',
      data
    });
  }

  delEnvironment(envId: number): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/del-environment/${envId}`,
      method: 'delete',
    });
  }

  getEnvironmentBaseList(): Promise<CommonResult<Array<EmsEnvironmentBase>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/environment-base-list`,
      method: 'get',
    })
  }

  getEnvironmentList(params: EmsEnvironmentQueryParam): Promise<CommonResult<CommonPage<EmsEnvironmentFull>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/environment-list`,
      method: 'get',
      params,
    })
  }

  updateEnvironment({environmentId, data}: {environmentId: number, data: EmsEnvironmentUpdateParam}): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/update-environment/${environmentId}`,
      method: 'post',
      data
    });
  }

  addEnvironmentUser(data: EmsEnvironmentUserRelationParam): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/add-environment-user`,
      method: 'post',
      data
    });
  }

  delEnvironmentUser(relationId: number): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/del-environment-user/${relationId}`,
      method: 'delete',
    });
  }

  getEnvironmentUserList({envId, params}: {envId: number, params: PageQueryParam}): Promise<CommonResult<CommonPage<EmsEnvironmentUserRelationFull>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/environment-user-list/${envId}`,
      method: 'get',
      params,
    })
  }

  updateEnvironmentUser({relationId, data}: {relationId: number, data: EmsEnvironmentUserRelationUpdateParam}): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/update-environment-user/${relationId}`,
      method: 'post',
      data
    });
  }
}
