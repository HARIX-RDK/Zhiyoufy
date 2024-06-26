import type { UmsRole } from "./UmsRole";


export interface UmsUserRoleUpdateData {
  addRoles: Array<UmsRole>;
  delRoles: Array<UmsRole>;
}
