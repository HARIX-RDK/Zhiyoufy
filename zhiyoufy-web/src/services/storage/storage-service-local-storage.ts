import type { StorageService } from "./storage-service";


export class LocalStorageService implements StorageService {
  constructor(private keyPrefix: string) {
  }

  loadInitialState() {
    return Object.keys(localStorage).reduce((state, storageKey) => {
      if (storageKey.startsWith(this.keyPrefix)) {
        const stateKeys = storageKey
          .replace(this.keyPrefix, '')
          .toLowerCase()
          .split('.')
          .map(key =>
            key
              .split('-')
              .map((token, index) =>
                index === 0
                  ? token
                  : token.charAt(0).toUpperCase() + token.slice(1)
              )
              .join('')
          );
        let currentStateRef = state;
        stateKeys.forEach((key, index) => {
          if (index === stateKeys.length - 1) {
            currentStateRef[key] = JSON.parse(localStorage.getItem(storageKey)!);
            return;
          }
          currentStateRef[key] = currentStateRef[key] || {};
          currentStateRef = currentStateRef[key];
        });
      }
      return state;
    }, {} as Record<string, any>);
  }

  setItem(key: string, value: any) {
    localStorage.setItem(`${this.keyPrefix}${key}`, JSON.stringify(value));
  }

  getItem(key: string) {
    const itemValue = localStorage.getItem(`${this.keyPrefix}${key}`);

    return itemValue == null ? itemValue : JSON.parse(itemValue);
  }

  removeItem(key: string) {
    localStorage.removeItem(`${this.keyPrefix}${key}`);
  }

  /** Tests that localStorage exists, can be written to, and read from. */
  testStorage() {
    const testValue = 'testValue';
    const testKey = 'testKey';
    const errorMessage = 'localStorage did not return expected value';

    this.setItem(testKey, testValue);
    const retrievedValue = this.getItem(testKey);
    this.removeItem(testKey);

    if (retrievedValue !== testValue) {
      throw new Error(errorMessage);
    }
  }
}

export function createLocalStorageService(keyPrefix: string) {
  const storageService = new LocalStorageService(keyPrefix);

  console.log(`created StorageService with keyPrefix ${keyPrefix}`, storageService);

  storageService.testStorage();

  return storageService;
}
