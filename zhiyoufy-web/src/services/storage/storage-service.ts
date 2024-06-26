export interface StorageService {
  loadInitialState(): Record<string, any>;
  setItem(key: string, value: any): void;
  getItem(key: string): any;
  removeItem(key: string): void;
  testStorage(): void;
}
