import type { UserApi } from "./user-api";
import type { CommonResult, CommonPage } from "@/model/dto/common";
import type {
  UmsUserQueryParam, UmsUserDTO, UmsUserBase, UpdateUserPasswordParam, UmsUserUpdateParam,
  FormLoginParam, LoginResponseData, UserInfoData,
  RequestIdentificationCodeParam, UmsUserParam,
  UmsPermission, UmsPermissionParam, UmsPermissionQueryParam,
  UmsRole, UmsRoleParam, UmsRoleQueryParam, UmsUserRoleUpdateData, UmsRolePermissionUpdateData
} from "@/model/dto/ums";

import { axiosInst } from "@/utils/request";
import { defaultSettings } from "@/settings";

const moduleApiPrefix = `${defaultSettings.apiPrefix}/user`;


export class UserApiImpl implements UserApi {
  addUser(data: UmsUserParam): Promise<CommonResult<UmsUserDTO>> {
    return axiosInst({
      url: `${moduleApiPrefix}/add-user`,
      method: 'post',
      data
    });
  }

  delUser(userId: number): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/del-user/${userId}`,
      method: 'delete',
    });
  }

  getUserBaseList(params?: UmsUserQueryParam): Promise<CommonResult<CommonPage<UmsUserBase>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/user-base-list`,
      method: 'get',
      params,
    })
  }

  getUserList(params?: UmsUserQueryParam): Promise<CommonResult<CommonPage<UmsUserDTO>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/user-list`,
      method: 'get',
      params,
    })
  }

  updateUser({ userId, data }: {userId: number, data: UmsUserUpdateParam}): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/update-user/${userId}`,
      method: 'post',
      data
    });
  }

  updateUserPassword(data: UpdateUserPasswordParam): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/update-user-password`,
      method: 'post',
      data
    });
  }

  getUserRoleList(userId: number): Promise<CommonResult<Array<UmsRole>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/user-role-list/${userId}`,
      method: 'get',
    })
  }

  updateUserRoleList({ userId, data }: {userId: number, data: UmsUserRoleUpdateData}): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/update-user-role-list/${userId}`,
      method: 'post',
      data
    });
  }

  formLogin(data: FormLoginParam): Promise<CommonResult<LoginResponseData>> {
    return axiosInst({
      url: `${moduleApiPrefix}/form-login`,
      method: 'post',
      data
    })
  }

  getUserInfo(): Promise<CommonResult<UserInfoData>> {
    return axiosInst({
      url: `${moduleApiPrefix}/user-info`,
      method: 'get',
    })
  }

  logout(): Promise<CommonResult<never>> {
    return axiosInst({
      url: `${moduleApiPrefix}/logout`,
      method: 'post'
    })
  }

  register(data: UmsUserParam): Promise<CommonResult<UmsUserDTO>> {
    return axiosInst({
      url: `${moduleApiPrefix}/register`,
      method: 'post',
      data
    })
  }

  requestIdCode(data: RequestIdentificationCodeParam): Promise<CommonResult<never>> {
    return axiosInst({
      url: `${moduleApiPrefix}/request-id-code`,
      method: 'post',
      data
    })
  }

  addPermission(data: UmsPermissionParam): Promise<CommonResult<UmsPermission>> {
    return axiosInst({
      url: `${moduleApiPrefix}/add-permission`,
      method: 'post',
      data
    });
  }

  delPermission(permissionId: number): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/del-permission/${permissionId}`,
      method: 'delete',
    });
  }

  getPermissionList(params?: UmsPermissionQueryParam): Promise<CommonResult<CommonPage<UmsPermission>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/permission-list`,
      method: 'get',
      params,
    })
  }

  updatePermission({permissionId, data}: {permissionId: number, data: Partial<UmsPermissionParam>}): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/update-permission/${permissionId}`,
      method: 'post',
      data
    });
  }

  addRole(data: UmsRoleParam): Promise<CommonResult<UmsRole>> {
    return axiosInst({
      url: `${moduleApiPrefix}/add-role`,
      method: 'post',
      data
    });
  }

  delRole(roleId: number): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/del-role/${roleId}`,
      method: 'delete',
    });
  }

  getRoleList(params?: UmsRoleQueryParam): Promise<CommonResult<CommonPage<UmsRole>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/role-list`,
      method: 'get',
      params,
    })
  }

  updateRole({roleId, data}: {roleId: number, data: Partial<UmsRoleParam>}): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/update-role/${roleId}`,
      method: 'post',
      data
    });
  }

  getRolePermissionList(roleId: number): Promise<CommonResult<Array<UmsPermission>>> {
    return axiosInst({
      url: `${moduleApiPrefix}/role-permission-list/${roleId}`,
      method: 'get',
    })
  }

  updateRolePermissionList({roleId, data}: {roleId: number, data: UmsRolePermissionUpdateData}): Promise<CommonResult<number>> {
    return axiosInst({
      url: `${moduleApiPrefix}/update-role-permission-list/${roleId}`,
      method: 'post',
      data
    });
  }
}
