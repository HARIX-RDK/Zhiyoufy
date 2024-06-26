import type { StorageService } from './storage-service';
import { createLocalStorageService } from './storage-service-local-storage';
import { defaultSettings } from '@/settings';


const storageKeyPrefix = defaultSettings.storageKeyPrefix;

export let gStorageService: StorageService = createLocalStorageService(storageKeyPrefix);

export function setGStorageService(inStorageService: StorageService) {
  gStorageService = inStorageService;
}

console.log('After Create StorageService');
