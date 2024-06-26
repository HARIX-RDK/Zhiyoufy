import { ref, watch } from 'vue'

import { defineStore } from 'pinia';

import type { PmsProjectBase } from '@/model/dto/pms';

import { gProjectApi } from '@/api';
import { gNotificationService, gStorageService } from '@/services';

const projectStateKey = 'projectState';

export const useProjectStore = defineStore('project', () => {
  const projectBaseList = ref<Array<PmsProjectBase>>([]);
  const selectedProjectBase = ref<PmsProjectBase>();

  async function refreshProjectBaseList(forceSync = false) {
    if (projectBaseList.value.length && !forceSync) {
      return;
    }

    const rsp = await gProjectApi.getProjectBaseList();

    if (rsp.error) {
      gNotificationService.error(rsp.error.message);
    } else if (rsp.data) {
      projectBaseList.value = rsp.data || [];
    }
  }

  function saveToStorage() {
    gStorageService.setItem(projectStateKey, {
      selectedProjectBase: selectedProjectBase.value,
    });
  }

  function restoreFromStorage() {
    const storedState = gStorageService.getItem(projectStateKey);

    if (storedState) {
      selectedProjectBase.value = storedState?.selectedProjectBase;
    }

    startWatchForChange();
  }

  function startWatchForChange() {
    watch([selectedProjectBase], () => {
      saveToStorage();
    })
  }

	return {
    projectBaseList,
    selectedProjectBase,

    refreshProjectBaseList,
    restoreFromStorage,
  }
});
