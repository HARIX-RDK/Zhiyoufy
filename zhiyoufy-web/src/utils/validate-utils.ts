export class ValidateUtils {
  static validateEmail(value: string): boolean {
    if (/^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(value)) {
      return true;
    }

    return false;
  }
}
