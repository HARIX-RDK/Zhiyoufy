import { ProjectApiImpl } from './project-api-impl';
import { JobFolderApiImpl } from './job-folder-api-impl';
import { JobTemplateApiImpl } from './job-template-api-impl';
import { FavoriteFolderApiImpl } from './favorite-folder-api-impl';

export * from './project-api';
export * from './project-api-impl';
export * from './job-folder-api';
export * from './job-folder-api-impl';
export * from './job-template-api';
export * from './job-template-api-impl';

export const gProjectApi = new ProjectApiImpl();
export const gJobFolderApi = new JobFolderApiImpl();
export const gJobTemplateApi = new JobTemplateApiImpl();
export const gFavoriteFolderApi = new FavoriteFolderApiImpl();
