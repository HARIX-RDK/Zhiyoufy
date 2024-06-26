import { ref, watch } from 'vue'

import { defineStore } from 'pinia';

import type { EmsEnvironmentBase } from '@/model/dto/ems';

import { gEnvironmentApi } from '@/api';
import { gNotificationService, gStorageService } from '@/services';

const environmentStateKey = 'environmentState';

export const useEnvironmentStore = defineStore('environment', () => {
  const environmentBaseList = ref<Array<EmsEnvironmentBase>>([]);
  const selectedEnvironmentBase = ref<EmsEnvironmentBase>();

  async function refreshEnvironmentBaseList() {
    const rsp = await gEnvironmentApi.getEnvironmentBaseList();

    if (rsp.error) {
      gNotificationService.error(rsp.error.message);
    } else if (rsp.data) {
      environmentBaseList.value = rsp.data || [];
    }
  }

  watch(environmentBaseList, () => {
    if (!selectedEnvironmentBase.value) {
      return;
    }

    const found = environmentBaseList.value.find((item) => {
      if (item.id === selectedEnvironmentBase.value?.id) {
        return true;
      }
      return false;
    });

    selectedEnvironmentBase.value = found;
  })

  function saveToStorage() {
    gStorageService.setItem(environmentStateKey, {
      selectedEnvironmentBase: selectedEnvironmentBase.value,
    });
  }

  function restoreFromStorage() {
    const storedState = gStorageService.getItem(environmentStateKey);

    if (storedState) {
      selectedEnvironmentBase.value = storedState.selectedEnvironmentBase;
    }

    startWatchForChange();
  }

  function startWatchForChange() {
    watch(selectedEnvironmentBase, () => {
      saveToStorage();
    })
  }

	return {
    environmentBaseList,
    selectedEnvironmentBase,

    refreshEnvironmentBaseList,
    restoreFromStorage,
  }
});
