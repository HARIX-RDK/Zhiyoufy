import type { JobFolderApi } from "./job-folder-api";

import type { CommonResult, CommonPage, DeleteInfo, UpdateInfo } from "@/model/dto/common";

import type { PmsJobFolder, PmsJobFolderParam, PmsJobFolderQueryParam, PmsJobFolderUpdateParam } from "@/model/dto/pms";

import { axiosInst } from "@/utils/request";
import { defaultSettings } from "@/settings";

const moduleApiPrefix = `${defaultSettings.apiPrefix}/job-folder`;


export class JobFolderApiImpl implements JobFolderApi {
  addJobFolder(data: PmsJobFolderParam): Promise<CommonResult<PmsJobFolder>> {
    return axiosInst({
      url: `${moduleApiPrefix}/add-job-folder`,
      method: 'post',
      data
    });
  }

  delJobFolder(jobFolderId: number): Promise<CommonResult<DeleteInfo>> {
    return axiosInst({
      url: `${moduleApiPrefix}/del-job-folder/${jobFolderId}`,
      method: 'delete',
    });
  }

  getJobFolderList(params: PmsJobFolderQueryParam): Promise<CommonResult<CommonPage<PmsJobFolder>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/job-folder-list`,
      method: 'get',
      params,
    })
  }

  updateJobFolder({jobFolderId, data}: {jobFolderId: number, data: PmsJobFolderUpdateParam}): Promise<CommonResult<UpdateInfo>> {
    return axiosInst({
      url: `${moduleApiPrefix}/update-job-folder/${jobFolderId}`,
      method: 'post',
      data
    });
  }
}
