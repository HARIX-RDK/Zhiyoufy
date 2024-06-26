import { ConfigCollectionApiImpl } from './config-collection-api-impl';
import { ConfigItemApiImpl } from './config-item-api-impl';
import { ConfigSingleApiImpl } from './config-single-api-impl';
import { EnvironmentApiImpl } from './environment-api-impl';

export * from './config-collection-api';
export * from './config-collection-api-impl';
export * from './config-item-api';
export * from './config-item-api-impl';
export * from './config-single-api';
export * from './config-single-api-impl';
export * from './environment-api';
export * from './environment-api-impl';

export const gConfigCollectionApi = new ConfigCollectionApiImpl();
export const gConfigItemApi = new ConfigItemApiImpl();
export const gConfigSingleApi = new ConfigSingleApiImpl();
export const gEnvironmentApi = new EnvironmentApiImpl();
