import type { ErrorInfo } from './ErrorInfo';


export interface CommonResult<T> {
  error?: ErrorInfo;
  data?: T;
}
