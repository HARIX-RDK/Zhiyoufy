import type { FavoriteFolderApi } from "./favorite-folder-api";

import type { CommonResult, DeleteInfo, UpdateInfo } from "@/model/dto/common";

import type { PmsFavoriteFolder, PmsFavoriteFolderParam, PmsFavoriteFolderUpdateParam,
  PmsFavoriteFolderTemplateRelation, PmsJobTemplate } from "@/model/dto/pms";

import { axiosInst } from "@/utils/request";
import { defaultSettings } from "@/settings";

const moduleApiPrefix = `${defaultSettings.apiPrefix}/favorite-folder`;


export class FavoriteFolderApiImpl implements FavoriteFolderApi {
  addFavoriteFolder(data: PmsFavoriteFolderParam): Promise<CommonResult<PmsFavoriteFolder>> {
    return axiosInst({
      url: `${moduleApiPrefix}/add-favorite-folder`,
      method: 'post',
      data
    });
  }

  delFavoriteFolder(favoriteFolderId: number): Promise<CommonResult<DeleteInfo>> {
    return axiosInst({
      url: `${moduleApiPrefix}/del-favorite-folder/${favoriteFolderId}`,
      method: 'delete',
    });
  }

  getFavoriteFolderList(projectId: number): Promise<CommonResult<Array<PmsFavoriteFolder>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/favorite-folder-list`,
      method: 'get',
      params: {projectId},
    })
  }

  updateFavoriteFolder({favoriteFolderId, data}: {favoriteFolderId: number, data: PmsFavoriteFolderUpdateParam}): Promise<CommonResult<UpdateInfo>> {
    return axiosInst({
      url: `${moduleApiPrefix}/update-favorite-folder/${favoriteFolderId}`,
      method: 'post',
      data
    });
  }

  addFolderTemplate(data: PmsFavoriteFolderTemplateRelation): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/add-folder-template`,
      method: 'post',
      data
    });
  }

  delFolderTemplate(data: PmsFavoriteFolderTemplateRelation): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/del-folder-template`,
      method: 'post',
      data
    });
  }

  getFolderTemplateList(folderId: number): Promise<CommonResult<Array<PmsJobTemplate>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/folder-template-list/${folderId}`,
      method: 'get',
    })
  }

  getTemplateFolderList(templateId: number): Promise<CommonResult<Array<PmsFavoriteFolder>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/favorite-folder-list/${templateId}`,
      method: 'get',
    })
  }
}
