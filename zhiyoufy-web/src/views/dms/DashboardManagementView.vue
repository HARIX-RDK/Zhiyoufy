<template>
  <div class="view-container">
    <div class="toolbar">
      <el-select
        v-model="projectName"
      >
        <el-option
          v-for="item in projectStore.projectBaseList"
          :key="item.id"
          :label="item.name"
          :value="item.name"
        />
      </el-select>

      <el-button class="ml-4"
        :icon="Refresh" @click="projectStore.refreshProjectBaseList(true)">
        Refresh
      </el-button>

      <div class="spacer"></div>

      <DatetimeRangePicker
        @datetime-range-changed="onDatetimeRangeChanged"
      >
      </DatetimeRangePicker>
    </div>
    <el-tabs v-if="projectStore.selectedProjectBase" v-model="activeTabName">
      <el-tab-pane label="General" name="General">
        <div class="tab">
          <GeneralDashboardPanel
            :visible="activeTabName === 'General'"
            :selected-project="projectStore.selectedProjectBase"
            :time-range="datetimeRange!"
          >
          </GeneralDashboardPanel>
        </div>
      </el-tab-pane>
      <el-tab-pane label="Environment" name="Environment">
        <div class="tab">
          <EnvironmentDashboardPanel
            :visible="activeTabName === 'Environment'"
          >
          </EnvironmentDashboardPanel>
        </div>
      </el-tab-pane>
      <el-tab-pane label="Template" name="Template">
        <div class="tab">
          <TemplateDashboardPanel
            :visible="activeTabName === 'Template'"
          >
          </TemplateDashboardPanel>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onBeforeMount, watch } from 'vue';

import { Refresh } from '@element-plus/icons-vue';

import DatetimeRangePicker from '@/components/datetime-range-picker/DatetimeRangePicker.vue';

import GeneralDashboardPanel from '@/components/dms/GeneralDashboardPanel.vue';
import EnvironmentDashboardPanel from '@/components/dms/EnvironmentDashboardPanel.vue';
import TemplateDashboardPanel from '@/components/dms/TemplateDashboardPanel.vue';

import type { TimeRange } from '@/model/datetime-range';

import { StrUtils } from '@/utils';

import { useProjectStore } from '@/stores/project';

const logPrefix = 'DashboardManagementView:';

const activeTabName = ref('General');

//#region Project
const projectStore = useProjectStore();

const projectName = ref('');

onBeforeMount(async () => {
  await projectStore.refreshProjectBaseList();

  if (projectStore.selectedProjectBase) {
    projectName.value = projectStore.selectedProjectBase.name;
  }
})

watch([projectName], ([value]) => {
  const found = projectStore.projectBaseList.find((item) => item.name === value);
  projectStore.selectedProjectBase = found;
  console.log(`selected project: ${StrUtils.pprint(projectStore.selectedProjectBase)}`);
});
//#endregion

//#region Datetime Range
const datetimeRange = ref<TimeRange>();

function onDatetimeRangeChanged(timeRange: TimeRange) {
  console.log(`${logPrefix} onDatetimeRangeChanged: timeRange ${StrUtils.pprint(timeRange)}`)
  datetimeRange.value = timeRange;
}
//#endregion
</script>

<style lang="scss" scoped>
.view-container {
  width: 100%;
  height: 100%;
  padding: 10px;

  .toolbar {
    margin-bottom: 16px;
    display: flex;
    flex-wrap: wrap;
    align-items: center;
  }

  .tab {
    // - header: 64px - padding: 20px - toolbar: 34px - toolbar margin bottom: 16px - tab header: 55px
    height: calc(var(--window-height) - 190px);
  }
}
</style>
