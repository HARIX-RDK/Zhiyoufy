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
    </div>
    <el-tabs v-if="projectStore.selectedProjectBase" v-model="activeTabName">
      <el-tab-pane label="Job Templates" name="Job Templates">
        <div class="tab">
          <JobTemplateManagePanel
          >
          </JobTemplateManagePanel>
        </div>
      </el-tab-pane>
      <el-tab-pane label="Active Job Runs" name="Active Job Runs">
        <div class="tab">
          <ActiveJobRunPanel
            :visible="activeTabName === 'Active Job Runs'"
          >
          </ActiveJobRunPanel>
        </div>
      </el-tab-pane>
      <el-tab-pane label="Finished Job Runs" name="Finished Job Runs">
        <div class="tab">
          <FinishedJobRunPanel
            :visible="activeTabName === 'Finished Job Runs'"
          >
          </FinishedJobRunPanel>
        </div>
      </el-tab-pane>
      <el-tab-pane label="Scheduled Job Runs" name="Scheduled Job Runs">
        <div class="tab">
          <ScheduledJobRunPanel
            :visible="activeTabName === 'Scheduled Job Runs'"
          >
          </ScheduledJobRunPanel>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onBeforeMount, watch } from 'vue';

import { Refresh } from '@element-plus/icons-vue';

import JobTemplateManagePanel from '@/components/pms/JobTemplateManagePanel.vue';
import ActiveJobRunPanel from '@/components/jms/ActiveJobRunPanel.vue';
import FinishedJobRunPanel from '@/components/jms/FinishedJobRunPanel.vue';
import ScheduledJobRunPanel from '@/components/jms/ScheduledJobRunPanel.vue';

import { useProjectStore } from '@/stores/project';

const projectStore = useProjectStore();

const activeTabName = ref('Job Templates');

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
  console.log(`selected project: ${JSON.stringify(projectStore.selectedProjectBase)}`);
});
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
    height: calc(var(--window-height) - 173px);
  }
}
</style>
