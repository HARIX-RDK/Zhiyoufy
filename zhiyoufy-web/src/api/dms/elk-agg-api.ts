import type { CommonResult } from "@/model/dto/common";

import type { AggDateHistogramReq, AggDateHistogramRsp, AggTermReq, AggTermRsp } from "@/model/dto/dms";


export interface ElkAggApi {
  createDateHistogram(data: AggDateHistogramReq): Promise<CommonResult<AggDateHistogramRsp>>;
  createTerm(data: AggTermReq): Promise<CommonResult<AggTermRsp>>;
}
