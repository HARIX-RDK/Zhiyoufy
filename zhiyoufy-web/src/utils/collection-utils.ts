export class CollectionUtils {
  static getFirst<T>(value: T | T[]): T {
    if (Array.isArray(value)) {
      return value[0];
    } else {
      return value;
    }
  }
}
