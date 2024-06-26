import type { BucketInfo } from './BucketInfo';

export interface AggTermRsp {
  total: number;
  totalPassed: number;
  totalNotPassed: number;
  buckets: Array<BucketInfo>;
}
