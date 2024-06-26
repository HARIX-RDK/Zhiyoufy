import type { NotificationParamsTyped, NotificationService } from './notification-service';

const logPrefix= 'notification:';


export class ConsoleNotificationService implements NotificationService {
  success(options: NotificationParamsTyped): void {
    this._notify('success', options);
  }

  warning(options: NotificationParamsTyped): void {
    this._notify('warning', options);
  }

  error(options: NotificationParamsTyped): void {
    this._notify('error', options);
  }

  info(options: NotificationParamsTyped): void {
    this._notify('info', options);
  }

  _notify(type: string, options: NotificationParamsTyped): void {
    let message;

    if (typeof(options) == 'string') {
      message = options;
    } else if (options.message) {
      message = options.message;
    } else {
      throw new Error('Invalid input');
    }

    console.log(`${logPrefix} type ${type}, message ${message}`);
  }
}
