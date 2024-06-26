import type { UmsPermission } from './UmsPermission';


export interface UmsRolePermissionUpdateData {
  addPermissions?: Array<UmsPermission>;
  delPermissions?: Array<UmsPermission>;
}
