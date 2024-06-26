import { ActiveWorkerApiImpl } from './active-worker-api-impl';
import { WorkerAppApiImpl } from './worker-app-api-impl';
import { WorkerGroupApiImpl } from './worker-group-api-impl';
import { GroupTokenApiImpl } from './group-token-api-impl';

export * from './active-worker-api';
export * from './active-worker-api-impl';
export * from './worker-app-api';
export * from './worker-app-api-impl';
export * from './worker-group-api';
export * from './worker-group-api-impl';
export * from './group-token-api';
export * from './group-token-api-impl';

export const gActiveWorkerApi = new ActiveWorkerApiImpl();
export const gWorkerAppApi = new WorkerAppApiImpl();
export const gWorkerGroupApi = new WorkerGroupApiImpl();
export const gGroupTokenApi = new GroupTokenApiImpl();
