import type { CommonResult } from "@/model/dto/common";
import type { FormLoginParam, LoginResponseData, UserInfoData } from "@/model/dto/ums";

export interface UserService {
  formLogin(data: FormLoginParam): Promise<CommonResult<LoginResponseData>>;
  getUserInfo(): Promise<CommonResult<UserInfoData>>;
  logout(): Promise<void>;
}
