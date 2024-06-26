import type { ErrorInfo } from "@/model/dto/common";

export function translateError(error: ErrorInfo) {
  if (error.detail) {
    return error.detail;
  } else {
    return error.message;
  }
}
