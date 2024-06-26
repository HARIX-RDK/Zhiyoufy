export class StrUtils {
  static pprint(obj: any): string {
    return JSON.stringify(obj, null, 2);
  }
}
