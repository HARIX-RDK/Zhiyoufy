import type { CommonResult, DeleteInfo, UpdateInfo } from "@/model/dto/common";

import type { PmsFavoriteFolder, PmsFavoriteFolderParam, PmsFavoriteFolderUpdateParam,
  PmsFavoriteFolderTemplateRelation, PmsJobTemplate } from "@/model/dto/pms";


export interface FavoriteFolderApi {
  addFavoriteFolder(data: PmsFavoriteFolderParam): Promise<CommonResult<PmsFavoriteFolder>>;
  delFavoriteFolder(favoriteFolderId: number): Promise<CommonResult<DeleteInfo>>;
  getFavoriteFolderList(projectId: number): Promise<CommonResult<Array<PmsFavoriteFolder>>>;
  updateFavoriteFolder({favoriteFolderId, data}: {favoriteFolderId: number, data: PmsFavoriteFolderUpdateParam}): Promise<CommonResult<UpdateInfo>>;

  addFolderTemplate(data: PmsFavoriteFolderTemplateRelation): Promise<CommonResult<number>>;
  delFolderTemplate(data: PmsFavoriteFolderTemplateRelation): Promise<CommonResult<number>>;
  getFolderTemplateList(folderId: number): Promise<CommonResult<Array<PmsJobTemplate>>>;
  getTemplateFolderList(templateId: number): Promise<CommonResult<Array<PmsFavoriteFolder>>>;
}
