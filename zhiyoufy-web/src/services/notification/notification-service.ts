import type { NotificationOptionsTyped } from 'element-plus';


export declare type NotificationParamsTyped = Partial<NotificationOptionsTyped> | string;

export interface NotificationService {
  success(options: NotificationParamsTyped): void;
  warning(options: NotificationParamsTyped): void;
  error(options: NotificationParamsTyped): void;
  info(options: NotificationParamsTyped): void;
}
