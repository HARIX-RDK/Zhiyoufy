export interface EmsConfigItemUpdateParam {
  name?: string;
  configValue?: string;
  tags?: string;
  sort?: number;
  disabled?: boolean;
  inUse?: boolean;
  usageId?: string;
  usageTimeoutAt?: string;
}
