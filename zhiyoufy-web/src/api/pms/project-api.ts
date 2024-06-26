import type { CommonResult, CommonPage, PageQueryParam } from "@/model/dto/common";

import type { PmsProject, PmsProjectParam, PmsProjectBase,
  PmsProjectQueryParam, PmsProjectFull, PmsProjectUpdateParam,
  PmsProjectUserRelationFull, PmsProjectUserRelationParam, PmsProjectUserRelationUpdateParam } from "@/model/dto/pms";


export interface ProjectApi {
  addProject(data: PmsProjectParam): Promise<CommonResult<PmsProject>>;
  delProject(projectId: number): Promise<CommonResult<number>>;
  getProjectBaseList(): Promise<CommonResult<Array<PmsProjectBase>>>;
  getProjectList(params: PmsProjectQueryParam): Promise<CommonResult<CommonPage<PmsProjectFull>>>;
  updateProject({projectId, data}: {projectId: number, data: PmsProjectUpdateParam}): Promise<CommonResult<number>>;

  addProjectUser(data: PmsProjectUserRelationParam): Promise<CommonResult<number>>;
  delProjectUser(relationId: number): Promise<CommonResult<number>>;
  getProjectUserList({projectId, params}: {projectId: number, params: PageQueryParam}): Promise<CommonResult<CommonPage<PmsProjectUserRelationFull>>>;
  updateProjectUser({relationId, data}: {relationId: number, data: PmsProjectUserRelationUpdateParam}): Promise<CommonResult<number>>;
}
