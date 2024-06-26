import type { NotificationService } from './notification-service';
import { ConsoleNotificationService } from './notification-service-console';


export let gNotificationService: NotificationService = new ConsoleNotificationService();

export function setGNotificationService(inNotificationService: NotificationService) {
  gNotificationService = inNotificationService;
}

console.log('After Create NotificationService');
