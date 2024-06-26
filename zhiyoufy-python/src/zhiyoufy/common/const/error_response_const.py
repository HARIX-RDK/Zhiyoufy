from zhiyoufy.common.models import ErrorResponse, ErrorInfo, BaseResultErrorType

RESPONSE_RES_ERR_INVALID = \
    ErrorResponse(error=ErrorInfo(err_enum=BaseResultErrorType.RES_ERR_INVALID))
