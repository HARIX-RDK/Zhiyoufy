<template>
  <div class="panel-container">
    <div class="dialog-content">
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
            v-for="item in environmentList"
            :key="item.id"
            :label="item.name"
            :value="item.name"
          />
        </el-select>

        <span class="ml-2">Template</span>
        <el-input
          class="ml-1 template-input"
          v-model="templateName"
          placeholder="template name"
        >
        </el-input>

        <span class="ml-2">RunTag</span>
        <el-input
          class="ml-1 runtag-input"
          v-model="runTag"
          placeholder="run tag"
        >
        </el-input>

        <div class="spacer"></div>

        <el-button type="primary" :icon="Search" @click="refreshJobScheduleList">
          Refresh
        </el-button>
      </div>

      <el-table :data="jobScheduleList" border class="table">
        <el-table-column prop="environmentName" label="Environment" width="120"></el-table-column>
        <el-table-column prop="templateName" label="JobTemplate"></el-table-column>
        <el-table-column prop="runTag" label="RunTag" width="110"></el-table-column>
        <el-table-column prop="runNum" label="RunNum" width="80" align="center"></el-table-column>
        <el-table-column prop="parallelNum" label="ParallelNum" width="110" align="center"></el-table-column>
        <el-table-column prop="crontabConfig" label="CrontabConfig" width="160"></el-table-column>
        <el-table-column prop="modifiedTime" label="ModifiedTime" width="160">
          <template #default="scope">
            {{ TimeUtils.displayTimeUptoSecs(scope.row.modifiedTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="modifiedBy" label="ModifiedBy" width="120"></el-table-column>

        <el-table-column label="Actions" width="210" align="center">
          <template #default="{ $index, row }">
            <el-button
              text :icon="Edit" @click="handleEdit($index, row)">
              Edit
            </el-button>

            <el-button
              text :icon="Delete" class="red" @click="handleDelete($index, row)">
              Delete
            </el-button>
          </template>
        </el-table-column>

        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="p-4">
              <div class="my-1">
                name: {{ row.name }}
              </div>
              <div class="my-1">
                includeTags: {{ row.includeTags }}
              </div>
              <div class="my-1">
                excludeTags: {{ row.excludeTags }}
              </div>
              <div class="my-1">
                addTags: {{ row.addTags }}
              </div>
              <div class="my-1">
                removeTags: {{ row.removeTags }}
              </div>
              <div class="my-1">
                createdTime: {{ row.createdTime }}
              </div>
              <div class="my-1">
                createdBy: {{ row.createdBy }}
              </div>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <div class="spacer"></div>
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 30]"
          :total="totalJobSchedules"

        ></el-pagination>
      </div>
    </div>

    <el-dialog
      v-if="jobScheduleFormDialogVisible"
      v-model="jobScheduleFormDialogVisible"
      :title="jobScheduleFormTitle"
    >
      <JobScheduleForm
        :job-schedule="editJobSchedule"
        @ok="onJobScheduleFormOk"
        @cancel="jobScheduleFormDialogVisible = false">
      </JobScheduleForm>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed, onBeforeUnmount  } from 'vue';

import { Edit, Delete, Search } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';

import JobScheduleForm from '@/components/jms/JobScheduleForm.vue';

import type { JmsJobSchedule, JmsJobScheduleQueryParam } from "@/model/dto/jms";
import type { EmsEnvironmentBase } from '@/model/dto/ems';

import { gJobScheduleApi } from '@/api';
import { TimeUtils } from '@/utils';

import { useProjectStore } from '@/stores/project';
import { useEnvironmentStore } from '@/stores/environment';

const logPrefix = 'ScheduledJobRunPanel:';

const props = defineProps({
  visible: {
    type: Boolean,
    required: true,
  }
});

const handling = ref(false);
const projectStore = useProjectStore();
const environmentStore = useEnvironmentStore();

environmentStore.refreshEnvironmentBaseList();

//#region Auto Refresh
let refreshInterval: number | undefined;

onBeforeUnmount(() => {
  clearInterval(refreshInterval);
})

watch(() => props.visible, (value) => {
  if (value) {
    console.log(`${logPrefix} start refresh interval`);

    refreshJobScheduleList();
    refreshInterval = window.setInterval(refreshJobScheduleList, 10_000);
  } else {
    console.log(`${logPrefix} stop refresh interval`);

    clearInterval(refreshInterval);
    refreshInterval = undefined;
  }
}, { immediate: true })
//#endregion

//#region JobSchedule List
const loading = ref(false);
const templateName = ref('');
const runTag = ref('');
const environmentName = ref('all');
const selectedEnvironmentBase = ref<EmsEnvironmentBase>();
const pageNum = ref(1);
const pageSize = ref(10);
const jobScheduleList = ref([] as JmsJobSchedule[]);
const totalJobSchedules = ref(0);

const allEnvironment = {
  id: 0,
  name: 'all',
} as EmsEnvironmentBase;

const environmentList = computed(() => {
  const targetEnvironmentList = [allEnvironment, ...environmentStore.environmentBaseList];
  return targetEnvironmentList;
})

watch(environmentName, (value) => {
  const found = environmentList.value.find((environmentBase) => {
    if (environmentBase.name === value) {
      return true;
    }
    return false;
  })
  selectedEnvironmentBase.value = found;
})

watch([pageNum, pageSize], () => {
  refreshJobScheduleList();
})

async function refreshJobScheduleList() {
  console.log(`${logPrefix} refreshJobScheduleList`);

  try {
    loading.value = true;

    const queryParam: JmsJobScheduleQueryParam = {
      projectId: projectStore.selectedProjectBase!.id,
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    };

    templateName.value && (queryParam.templateName = templateName.value);
    runTag.value && (queryParam.runTag = runTag.value);
    selectedEnvironmentBase.value?.id && (queryParam.environmentId = selectedEnvironmentBase.value.id);

    const rsp = await gJobScheduleApi.getJobScheduleList(queryParam);

    jobScheduleList.value = rsp.data?.list || [];
    totalJobSchedules.value = rsp.data?.total || 0;
  } finally {
    loading.value= false;
  }
}
//#endregion

//#region JobSchedule List Item
const jobScheduleFormDialogVisible = ref(false);
const jobScheduleFormTitle = ref('Edit JobSchedule');
const editJobSchedule = ref<JmsJobSchedule>();

watch([jobScheduleFormDialogVisible], ([value]) => {
  if (!value) {
    editJobSchedule.value = undefined;
  }
});

function handleEdit(index: number, row: JmsJobSchedule) {
  console.log(`handleEdit: index ${index}, env ${row.environmentName}, template ${row.templateName}`);
  editJobSchedule.value = row;
  jobScheduleFormDialogVisible.value = true;
}

async function handleDelete(index: number, row: JmsJobSchedule) {
  console.log(`handleDelete: index ${index}, row ${row.name}`);

  try {
    handling.value = true;

    const action = await ElMessageBox.confirm(
      `Are you sure to delete jobSchedule for env ${row.environmentName}, template ${row.templateName}. Continue?`,
      'Warning',
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
    );

    if (action === 'confirm') {
      await gJobScheduleApi.delJobSchedule(row.id);

      await refreshJobScheduleList();
    }
  }
  finally {
    handling.value = false;
  }
}

function onJobScheduleFormOk() {
  jobScheduleFormDialogVisible.value = false;
  refreshJobScheduleList();
}
//#endregion
</script>

<style lang="scss" scoped>
.panel-container {
  width: 100%;
  height: 100%;
  overflow: auto;

  .toolbar {
    margin-bottom: 16px;
    display: flex;
    flex-wrap: wrap;
    align-items: center;

    .select-environment {
      width: 160px;
    }

    .template-input {
      width: 200px;
    }

    .runtag-input {
      width: 160px;
    }
  }
  .pagination {
    margin-top: 10px;
    display: flex;
  }
}
</style>
