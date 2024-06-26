import type { UserService } from './user-service';
import { UserServiceImpl } from './user-service-impl';


export let gUserService: UserService = new UserServiceImpl();

export function setGUserService(inUserService: UserService) {
  gUserService = inUserService;
}

console.log('After Create UserService');
