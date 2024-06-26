export interface EmsConfigItem {
  id: number;
  environmentId: number;
  collectionId: number;
  name: string;
  tags: string;
  sort: number;
  disabled: boolean;
  inUse: boolean;
  usageId: string;
  usageTimeoutAt: string;
  configValue: string;
}
