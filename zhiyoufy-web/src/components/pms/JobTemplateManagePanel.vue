<template>
  <div class="container">
    <div class="toolbar">
      <span>
        Environment
      </span>
      <el-select
        class="ml-1 select-environment"
        v-model="environmentName"
        filterable
      >
        <el-option
          v-for="item in environmentStore.environmentBaseList"
          :key="item.id"
          :label="item.name"
          :value="item.name"
        />
      </el-select>

      <span class="ml-2">
        WorkerApp
      </span>
      <el-select
        class="ml-1 select-worker-app"
        v-model="workerAppName"
      >
        <el-option
          v-for="item in workerStore.workerAppBaseList"
          :key="item.id"
          :label="item.name"
          :value="item.name"
        />
      </el-select>

      <span class="ml-2">
        WorkerGroup
      </span>
      <el-select
        class="ml-1 select-worker-group"
        v-model="workerGroupName"
      >
        <el-option
          v-for="item in workerGroupBaseListFiltered"
          :key="item.id"
          :label="item.name"
          :value="item.name"
        />
      </el-select>

      <el-button class="ml-4"
        :icon="Refresh" @click="refreshAll">
        Refresh
      </el-button>

      <div class="spacer"></div>

      <el-button class="ml-4" type="primary" :disabled="!okToRun"
        @click="jobRunStartFormDialogVisible = true">
        Run
      </el-button>

      <el-button class="ml-4" :disabled="!okToRun"
        @click="jobScheduleFormDialogVisible = true">
        Schedule
      </el-button>
    </div>

    <div class="main-content">
      <el-tabs v-model="activeTabName">
        <el-tab-pane label="Main" name="Main">
          <div class="tab">
            <JobTemplateManagePanelMain
              @selected-job-template="onMainSelectedJobTemplate"
            >
            </JobTemplateManagePanelMain>
          </div>
        </el-tab-pane>

        <el-tab-pane label="Favorite" name="Favorite">
          <div class="tab">
            <JobTemplateManagePanelFavorite
              @selected-job-template="onFavoriteSelectedJobTemplate"
            >
            </JobTemplateManagePanelFavorite>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <el-dialog
      v-if="jobRunStartFormDialogVisible"
      v-model="jobRunStartFormDialogVisible"
      :title="jobRunStartFormDialogTitle"
      top="5vh"
    >
      <JobRunStartForm
        :project-base="projectStore.selectedProjectBase!"
        :environment-base="selectedEnvironmentBase"
        :worker-app-base="selectedWorkerAppBase"
        :active-worker-group-base="selectedActiveWorkerGroupBase"
        :job-template="selectedJobTemplate"
        @ok="jobRunStartFormDialogVisible = false" @cancel="jobRunStartFormDialogVisible = false">
      </JobRunStartForm>
    </el-dialog>

    <el-dialog
      v-if="jobScheduleFormDialogVisible"
      v-model="jobScheduleFormDialogVisible"
      :title="jobScheduleFormDialogTitle"
      top="5vh"
    >
      <JobScheduleForm
        :project-base="projectStore.selectedProjectBase!"
        :environment-base="selectedEnvironmentBase"
        :worker-app-base="selectedWorkerAppBase"
        :active-worker-group-base="selectedActiveWorkerGroupBase"
        :job-template="selectedJobTemplate"
        @ok="jobScheduleFormDialogVisible = false" @cancel="jobScheduleFormDialogVisible = false">
      </JobScheduleForm>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onBeforeMount, watch, computed } from 'vue';

import { Refresh } from '@element-plus/icons-vue';

import JobTemplateManagePanelMain from '@/components/pms/JobTemplateManagePanelMain.vue';
import JobTemplateManagePanelFavorite from '@/components/pms/JobTemplateManagePanelFavorite.vue';

import JobRunStartForm from '@/components/jms/JobRunStartForm.vue';
import JobScheduleForm from '@/components/jms/JobScheduleForm.vue';

import type { PmsJobTemplate } from '@/model/dto/pms';
import type { WmsActiveWorkerGroupBase, WmsWorkerAppBase } from '@/model/dto/wms';
import type { EmsEnvironmentBase } from '@/model/dto/ems';

import { storeToRefs } from 'pinia'

import { useWorkerStore } from '@/stores/worker';
import { useEnvironmentStore } from '@/stores/environment';
import { useProjectStore } from '@/stores/project';

const projectStore = useProjectStore();

const workerStore = useWorkerStore();

const { selectedWorkerAppBase, selectedActiveWorkerGroupBase } = storeToRefs(workerStore);

const activeTabName = ref('Main');

onBeforeMount(() => {
  refreshAll();
})

//#region Actions
const jobRunStartFormDialogVisible = ref(false);
const jobRunStartFormDialogTitle = ref('Start JobRun');
const jobScheduleFormDialogVisible = ref(false);
const jobScheduleFormDialogTitle = ref('New JobSchedule');

const okToRun = computed(() => {
  if (!selectedJobTemplate.value || !selectedEnvironmentBase.value ||
    !selectedWorkerAppBase.value || !selectedActiveWorkerGroupBase.value) {
    return false;
  }
  if (!jobTemplateAndWorkerAppLabelMatch(selectedJobTemplate.value, selectedWorkerAppBase.value)) {
    return false;
  }
  return true;
})

async function refreshAll() {
  await environmentStore.refreshEnvironmentBaseList();
  await workerStore.refreshWorkerAppBaseList();
  await workerStore.refreshActiveWorkerGroupBaseList();
}
//#endregion

//#region Environment
const environmentStore = useEnvironmentStore();

const { selectedEnvironmentBase } = storeToRefs(environmentStore);

environmentStore.refreshEnvironmentBaseList();

const environmentName = ref(selectedEnvironmentBase.value?.name);

watch(environmentName, (value) => {
  if (!value) {
    return;
  }

  console.log('environmentName changed to ', value);

  const found = environmentStore.environmentBaseList.find(item => {
    if (item.name === value) {
      return true;
    }
    return false;
  })
  selectedEnvironmentBase.value = found;
})

watch(selectedEnvironmentBase, () => {
  environmentName.value = selectedEnvironmentBase.value?.name;
})
//#endregion

//#region Worker
const workerAppName = ref(selectedWorkerAppBase.value?.name);
const workerGroupName = ref(selectedActiveWorkerGroupBase.value?.name);

const workerGroupBaseListFiltered = computed(() => {
  if (!selectedEnvironmentBase.value) {
    return [];
  }

  let targetWorkerGroupBaseList = [...workerStore.activeWorkerGroupBaseList];

  if (selectedEnvironmentBase.value.workerLabels) {
    targetWorkerGroupBaseList = targetWorkerGroupBaseList.filter(workerGroupBase => {
      return envAndWorkerGroupLabelMatch(selectedEnvironmentBase.value!, workerGroupBase);
    })
  }

  return targetWorkerGroupBaseList;
});

watch(workerAppName, (value) => {
  if (!value) {
    return;
  }

  console.log('workerAppName changed to ', value);
  const found = workerStore.workerAppBaseList.find(appBase => {
    if (appBase.name === value) {
      return true;
    }
    return false;
  })
  selectedWorkerAppBase.value = found;
})

watch(selectedWorkerAppBase, () => {
  workerAppName.value = selectedWorkerAppBase.value?.name;
})

watch(workerGroupName, (value) => {
  if (!value) {
    return;
  }

  console.log('workerGroupName changed to ', value);
  const found = workerStore.activeWorkerGroupBaseList.find(groupBase => {
    if (groupBase.name === value) {
      return true;
    }
    return false;
  })
  selectedActiveWorkerGroupBase.value = found;
})

watch(selectedActiveWorkerGroupBase, () => {
  workerGroupName.value = selectedActiveWorkerGroupBase.value?.name;
})

watch(workerGroupBaseListFiltered, () => {
  if (!selectedActiveWorkerGroupBase.value) {
    return;
  }

  const found = workerGroupBaseListFiltered.value.find((item) => {
    if (item.id === selectedActiveWorkerGroupBase.value?.id) {
      return true;
    }
    return false;
  });

  selectedActiveWorkerGroupBase.value = found;
})

function envAndWorkerGroupLabelMatch(environmentBase: EmsEnvironmentBase, workerGroupBase: WmsActiveWorkerGroupBase) {
  const envWorkerLabels = JSON.parse(environmentBase.workerLabels);
  const workerGroupLabels = JSON.parse(workerGroupBase.workerLabels);

  for(let labelName of Object.keys(envWorkerLabels)) {
    if(!(labelName in workerGroupLabels)) {
      return false;
    }
  }

  return true;
}
//#endregion

//#region Job Template
const mainSelectedJobTemplate = ref<PmsJobTemplate>();
const favoriteSelectedJobTemplate = ref<PmsJobTemplate>();

const selectedJobTemplate = computed(() => {
  if (activeTabName.value === 'Main') {
    return mainSelectedJobTemplate.value;
  } else if (activeTabName.value === 'Favorite') {
    return favoriteSelectedJobTemplate.value;
  }
  return undefined;
})

function onMainSelectedJobTemplate(value: PmsJobTemplate) {
  mainSelectedJobTemplate.value = value;
}

function onFavoriteSelectedJobTemplate(value: PmsJobTemplate) {
  favoriteSelectedJobTemplate.value = value;
}

function jobTemplateAndWorkerAppLabelMatch(jobTemplate: PmsJobTemplate, workerAppBase: WmsWorkerAppBase) {
  const jobTemplateLabels = JSON.parse(jobTemplate.workerLabels);
  const workerAppLabels = JSON.parse(workerAppBase.workerLabels);

  for(let labelName of Object.keys(jobTemplateLabels)) {
    if(!(labelName in workerAppLabels)) {
      return false;
    }
  }

  return true;
}
//#endregion
</script>

<style lang="scss" scoped>
.container {
  width: 100%;
  height: 100%;
  overflow-y: auto;

  .toolbar {
    margin-bottom: 8px;
    display: flex;
    flex-wrap: wrap;
    align-items: center;
  }

  .select-worker-app, .select-worker-group, .select-environment {
    width: 160px;
  }
}
</style>
