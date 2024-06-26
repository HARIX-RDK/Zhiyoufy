import { UserApiImpl } from './user-api-impl';

export * from './user-api';
export * from './user-api-impl';

export const gUserApi = new UserApiImpl();
