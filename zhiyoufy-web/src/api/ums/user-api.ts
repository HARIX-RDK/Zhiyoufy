import type { CommonResult, CommonPage } from "@/model/dto/common";
import type {
  UmsUserQueryParam, UmsUserDTO, UmsUserBase, UpdateUserPasswordParam, UmsUserUpdateParam,
  FormLoginParam, LoginResponseData, UserInfoData,
  RequestIdentificationCodeParam, UmsUserParam,
  UmsPermission, UmsPermissionParam, UmsPermissionQueryParam,
  UmsRole, UmsRoleParam, UmsRoleQueryParam, UmsUserRoleUpdateData, UmsRolePermissionUpdateData
} from "@/model/dto/ums";


export interface UserApi {
  addUser(data: UmsUserParam): Promise<CommonResult<UmsUserDTO>>;
  delUser(userId: number): Promise<CommonResult<number>>;
  getUserBaseList(params?: UmsUserQueryParam): Promise<CommonResult<CommonPage<UmsUserBase>>>;
  getUserList(params?: UmsUserQueryParam): Promise<CommonResult<CommonPage<UmsUserDTO>>>;
  updateUser({ userId, data }: {userId: number, data: UmsUserUpdateParam}): Promise<CommonResult<number>>;
  updateUserPassword(data: UpdateUserPasswordParam): Promise<CommonResult<number>>;

  getUserRoleList(userId: number): Promise<CommonResult<Array<UmsRole>>>;
  updateUserRoleList({ userId, data }: {userId: number, data: UmsUserRoleUpdateData}): Promise<CommonResult<number>>;

  formLogin(data: FormLoginParam): Promise<CommonResult<LoginResponseData>>;
  getUserInfo(): Promise<CommonResult<UserInfoData>>;
  logout(): Promise<CommonResult<never>>;

  register(data: UmsUserParam): Promise<CommonResult<UmsUserDTO>>;
  requestIdCode(data: RequestIdentificationCodeParam): Promise<CommonResult<never>>;

  addPermission(data: UmsPermissionParam): Promise<CommonResult<UmsPermission>>;
  delPermission(permissionId: number): Promise<CommonResult<number>>;
  getPermissionList(params?: UmsPermissionQueryParam): Promise<CommonResult<CommonPage<UmsPermission>>>;
  updatePermission({permissionId, data}: {permissionId: number, data: Partial<UmsPermissionParam>}): Promise<CommonResult<number>>;

  addRole(data: UmsRoleParam): Promise<CommonResult<UmsRole>>;
  delRole(roleId: number): Promise<CommonResult<number>>;
  getRoleList(params?: UmsRoleQueryParam): Promise<CommonResult<CommonPage<UmsRole>>>;
  updateRole({roleId, data}: {roleId: number, data: Partial<UmsRoleParam>}): Promise<CommonResult<number>>;

  getRolePermissionList(roleId: number): Promise<CommonResult<Array<UmsPermission>>>;
  updateRolePermissionList({roleId, data}: {roleId: number, data: UmsRolePermissionUpdateData}): Promise<CommonResult<number>>;
}
