import type { CommonResult, CommonPage, PageQueryParam } from "@/model/dto/common";

import type { EmsEnvironment, EmsEnvironmentParam, EmsEnvironmentBase,
  EmsEnvironmentQueryParam, EmsEnvironmentFull, EmsEnvironmentUpdateParam,
  EmsEnvironmentUserRelationFull, EmsEnvironmentUserRelationParam, EmsEnvironmentUserRelationUpdateParam } from "@/model/dto/ems";


export interface EnvironmentApi {
  addEnvironment(data: EmsEnvironmentParam): Promise<CommonResult<EmsEnvironment>>;
  delEnvironment(envId: number): Promise<CommonResult<number>>;
  getEnvironmentBaseList(): Promise<CommonResult<Array<EmsEnvironmentBase>>>;
  getEnvironmentList(params: EmsEnvironmentQueryParam): Promise<CommonResult<CommonPage<EmsEnvironmentFull>>>;
  updateEnvironment({environmentId, data}: {environmentId: number, data: EmsEnvironmentUpdateParam}): Promise<CommonResult<number>>;

  addEnvironmentUser(data: EmsEnvironmentUserRelationParam): Promise<CommonResult<number>>;
  delEnvironmentUser(relationId: number): Promise<CommonResult<number>>;
  getEnvironmentUserList({envId, params}: {envId: number, params: PageQueryParam}): Promise<CommonResult<CommonPage<EmsEnvironmentUserRelationFull>>>;
  updateEnvironmentUser({relationId, data}: {relationId: number, data: EmsEnvironmentUserRelationUpdateParam}): Promise<CommonResult<number>>;
}
