import type { BucketInfo } from './BucketInfo';


export interface AggDateHistogramRsp {
  total: number;
  totalPassed: number;
  totalNotPassed: number;
  buckets: Array<BucketInfo>;
}
