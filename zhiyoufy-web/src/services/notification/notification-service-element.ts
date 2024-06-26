import { ElNotification } from 'element-plus'

import type { NotificationParamsTyped, NotificationService } from './notification-service';


export class ElementNotificationService implements NotificationService {
  success(options: NotificationParamsTyped): void {
    ElNotification.success(options);
  }

  warning(options: NotificationParamsTyped): void {
    ElNotification.warning(options);
  }

  error(options: NotificationParamsTyped): void {
    ElNotification.error(options);
  }

  info(options: NotificationParamsTyped): void {
    ElNotification.info(options);
  }
}
