export interface UmsUserDTO {
  id: number;
  username: string;
  email: string;
  note: string;
  createTime: string;
  loginTime?: string;
  enabled: boolean;
  sysAdmin: boolean;
  admin: boolean;
}
