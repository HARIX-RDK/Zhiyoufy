import { ref, watch } from 'vue'

import { defineStore } from 'pinia';

import type { WmsWorkerAppBase, WmsActiveWorkerGroupBase } from '@/model/dto/wms';

import { gActiveWorkerApi, gWorkerAppApi } from '@/api';
import { gNotificationService, gStorageService } from '@/services';

const workerStateKey = 'workerState';

export const useWorkerStore = defineStore('worker', () => {
  const workerAppBaseList = ref<Array<WmsWorkerAppBase>>([]);
  const selectedWorkerAppBase = ref<WmsWorkerAppBase>();
  const activeWorkerGroupBaseList = ref<Array<WmsActiveWorkerGroupBase>>([]);
  const selectedActiveWorkerGroupBase = ref<WmsActiveWorkerGroupBase>();

  async function refreshWorkerAppBaseList() {
    const rsp = await gWorkerAppApi.getWorkerAppBaseList();

    if (rsp.error) {
      gNotificationService.error(rsp.error.message);
      return;
    } else {
      workerAppBaseList.value = rsp.data || [];
    }
  }

  watch(workerAppBaseList, () => {
    if (!selectedWorkerAppBase.value) {
      return;
    }

    const found = workerAppBaseList.value.find((item) => {
      if (item.id === selectedWorkerAppBase.value?.id) {
        return true;
      }
      return false;
    });

    selectedWorkerAppBase.value = found;
  })

  watch(selectedWorkerAppBase, () => {
    refreshActiveWorkerGroupBaseList();
  })

  let groupBaseListLastReqTime;

  async function refreshActiveWorkerGroupBaseList() {
    const reqTime = Date.now();
    groupBaseListLastReqTime = reqTime;

    if (!selectedWorkerAppBase.value) {
      activeWorkerGroupBaseList.value = [];
      return;
    }

    const workerAppId = selectedWorkerAppBase.value.id;

    const rsp = await gActiveWorkerApi.getGroupBaseList(workerAppId);

    if (reqTime !== groupBaseListLastReqTime) {
      return;
    }

    if (rsp.error) {
      gNotificationService.error(rsp.error.message);
      activeWorkerGroupBaseList.value = [];
      return;
    } else {
      activeWorkerGroupBaseList.value = rsp.data || [];
    }
  }

  watch(activeWorkerGroupBaseList, () => {
    if (!selectedActiveWorkerGroupBase.value) {
      return;
    }

    const found = activeWorkerGroupBaseList.value.find((item) => {
      if (item.id === selectedActiveWorkerGroupBase.value?.id) {
        return true;
      }
      return false;
    });

    selectedActiveWorkerGroupBase.value = found;
  })

  function saveToStorage() {
    gStorageService.setItem(workerStateKey, {
      selectedWorkerAppBase: selectedWorkerAppBase.value,
      selectedActiveWorkerGroupBase: selectedActiveWorkerGroupBase.value,
    });
  }

  function restoreFromStorage() {
    const storedState = gStorageService.getItem(workerStateKey);

    console.log('workerState', storedState)

    if (storedState) {
      selectedWorkerAppBase.value = storedState?.selectedWorkerAppBase;
      selectedActiveWorkerGroupBase.value = storedState?.selectedActiveWorkerGroupBase;
    }

    startWatchForChange();
  }

  function startWatchForChange() {
    watch(selectedWorkerAppBase, (newValue, oldValue) => {
      if (newValue?.id !== oldValue?.id) {
        activeWorkerGroupBaseList.value = [];
        selectedActiveWorkerGroupBase.value = undefined;

        refreshActiveWorkerGroupBaseList();
      }
    })

    watch([selectedWorkerAppBase, selectedActiveWorkerGroupBase], () => {
      saveToStorage();
    })
  }

	return {
    workerAppBaseList,
    selectedWorkerAppBase,
    activeWorkerGroupBaseList,
    selectedActiveWorkerGroupBase,

    refreshWorkerAppBaseList,
    refreshActiveWorkerGroupBaseList,
    restoreFromStorage,
  }
});
