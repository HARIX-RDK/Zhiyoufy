<template>
  <div class="component-container">
    <div class="input-row">
      PerfParallelNum: {{ activeJobRunBase!.perfParallelNum }}
    </div>

    <div class="input-row">
      <el-input-number :min="0"
        class="ml-2"
        v-model="perfParallelNum">
      </el-input-number>

      <el-button type="primary" class="ml-2" @click="updatePerfParallelNum">
        Update PerfParallelNum
      </el-button>
    </div>

    <div class="input-row">
      DashboardAddr:
      <a :href="activeJobRunBase!.dashboardAddr" class="ml-2" target="_blank">
        {{ activeJobRunBase!.dashboardAddr }}
      </a>
    </div>

    <div class="input-row">
      WorkerNames: {{ activeJobRunBase!.workerNames }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onBeforeMount, onBeforeUnmount } from 'vue';
import type { PropType } from 'vue';

import type { JmsActiveJobRunBase, JmsJobRunUpdatePerfParallelNumReq } from "@/model/dto/jms";

import { gNotificationService } from '@/services';
import { gJobRunApi } from '@/api';

const props = defineProps({
  jobRun: {
    type: Object as PropType<JmsActiveJobRunBase>,
    required: true,
  }
});

const logPrefix = 'PerfJobRunDialog:';

const loading = ref(false);
let runGuid: string;
const activeJobRunBase = ref<JmsActiveJobRunBase>();

onBeforeMount(() => {
  runGuid = props.jobRun.runGuid;
  activeJobRunBase.value = props.jobRun;
  perfParallelNum.value = props.jobRun.perfParallelNum;
})

//#region Auto Refresh
let refreshInterval: number | undefined;
let refreshReqTime;

onBeforeMount(() => {
  refreshInterval = window.setInterval(refreshActiveJobRunBase, 10_000);
})

onBeforeUnmount(() => {
  clearInterval(refreshInterval);
})

async function refreshActiveJobRunBase() {
  const reqTime = Date.now();
  refreshReqTime = reqTime;

  console.log(`refreshActiveJobRunBase`);

  const rsp = await gJobRunApi.getActiveJobRunBaseSingle(runGuid);

  if (refreshReqTime !== reqTime) {
    return;
  }

  if (rsp.error) {
    gNotificationService.error(rsp.error.message);
  } else if (!rsp.data) {
    gNotificationService.error("no activeJobRunBase found");
  } else {
    activeJobRunBase.value = rsp.data;
  }
}
//#endregion

//#region Update PerfParallelNum
const perfParallelNum = ref(0);

async function updatePerfParallelNum() {
  console.log(`${logPrefix} updatePerfParallelNum`);

  try {
    loading.value = true;

    const updateReq: JmsJobRunUpdatePerfParallelNumReq = {
      runGuid: props.jobRun.runGuid,
      perfParallelNum: perfParallelNum.value,
    };

    const rsp = await gJobRunApi.updatePerfParallelNum(updateReq);

    if (rsp.error) {
      gNotificationService.error(rsp.error.message);
    } else {
      await refreshActiveJobRunBase();
    }
  } finally {
    loading.value= false;
  }
}
//#endregion
</script>

<style lang="scss" scoped>
.component-container {
  width: 100%;
  height: 100%;

  .input-row {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
  }
}
</style>
