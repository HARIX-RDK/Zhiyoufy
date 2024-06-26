import type { ProjectApi } from "./project-api";
import type { CommonResult, CommonPage, PageQueryParam } from "@/model/dto/common";

import type { PmsProject, PmsProjectParam, PmsProjectBase,
  PmsProjectQueryParam, PmsProjectFull, PmsProjectUpdateParam,
  PmsProjectUserRelationFull, PmsProjectUserRelationParam, PmsProjectUserRelationUpdateParam } from "@/model/dto/pms";

import { axiosInst } from "@/utils/request";
import { defaultSettings } from "@/settings";

const moduleApiPrefix = `${defaultSettings.apiPrefix}/project`;


export class ProjectApiImpl implements ProjectApi {
  addProject(data: PmsProjectParam): Promise<CommonResult<PmsProject>> {
    return axiosInst({
      url: `${moduleApiPrefix}/add-project`,
      method: 'post',
      data
    });
  }

  delProject(projectId: number): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/del-project/${projectId}`,
      method: 'delete',
    });
  }

  getProjectBaseList(): Promise<CommonResult<Array<PmsProjectBase>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/project-base-list`,
      method: 'get',
    })
  }

  getProjectList(params: PmsProjectQueryParam): Promise<CommonResult<CommonPage<PmsProjectFull>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/project-list`,
      method: 'get',
      params,
    })
  }

  updateProject({projectId, data}: {projectId: number, data: PmsProjectUpdateParam}): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/update-project/${projectId}`,
      method: 'post',
      data
    });
  }

  addProjectUser(data: PmsProjectUserRelationParam): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/add-project-user`,
      method: 'post',
      data
    });
  }

  delProjectUser(relationId: number): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/del-project-user/${relationId}`,
      method: 'delete',
    });
  }

  getProjectUserList({projectId, params}: {projectId: number, params: PageQueryParam}): Promise<CommonResult<CommonPage<PmsProjectUserRelationFull>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/project-user-list/${projectId}`,
      method: 'get',
      params,
    })
  }

  updateProjectUser({relationId, data}: {relationId: number, data: PmsProjectUserRelationUpdateParam}): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/update-project-user/${relationId}`,
      method: 'post',
      data
    });
  }
}
