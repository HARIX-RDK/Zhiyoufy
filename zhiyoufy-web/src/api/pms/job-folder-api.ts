import type { CommonResult, CommonPage, DeleteInfo, UpdateInfo } from "@/model/dto/common";

import type { PmsJobFolder, PmsJobFolderParam,
  PmsJobFolderQueryParam, PmsJobFolderUpdateParam } from "@/model/dto/pms";


export interface JobFolderApi {
  addJobFolder(data: PmsJobFolderParam): Promise<CommonResult<PmsJobFolder>>;
  delJobFolder(jobFolderId: number): Promise<CommonResult<DeleteInfo>>;
  getJobFolderList(params: PmsJobFolderQueryParam): Promise<CommonResult<CommonPage<PmsJobFolder>>>;
  updateJobFolder({jobFolderId, data}: {jobFolderId: number, data: PmsJobFolderUpdateParam}): Promise<CommonResult<UpdateInfo>>;
}
