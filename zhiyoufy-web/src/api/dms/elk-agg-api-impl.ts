import type { ElkAggApi } from "./elk-agg-api";
import type { CommonResult } from "@/model/dto/common";

import type { AggDateHistogramReq, AggDateHistogramRsp, AggTermReq, AggTermRsp } from "@/model/dto/dms";

import { axiosInst } from "@/utils/request";
import { defaultSettings } from "@/settings";

const moduleApiPrefix = `${defaultSettings.apiPrefix}/elk-agg`;


export class ElkAggApiImpl implements ElkAggApi {
  createDateHistogram(data: AggDateHistogramReq): Promise<CommonResult<AggDateHistogramRsp>> {
    return axiosInst({
      url: `${moduleApiPrefix}/datehistogram`,
      method: 'post',
      data
    });
  }

  createTerm(data: AggTermReq): Promise<CommonResult<AggTermRsp>> {
    return axiosInst({
      url: `${moduleApiPrefix}/term`,
      method: 'post',
      data
    });
  }
}
