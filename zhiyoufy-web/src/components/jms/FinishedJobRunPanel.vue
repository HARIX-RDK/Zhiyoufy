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

        <el-button type="primary" :icon="Search" @click="refreshJobRunResultList">
          Refresh
        </el-button>
      </div>

      <el-table :data="jobRunResultList" border class="table">
        <el-table-column prop="environmentName" label="Environment" width="120"></el-table-column>
        <el-table-column prop="templateName" label="JobTemplate"></el-table-column>
        <el-table-column prop="username" label="Username" width="120"></el-table-column>
        <el-table-column prop="runTag" label="RunTag" width="110"></el-table-column>
        <el-table-column prop="runNum" label="RunNum" width="80" align="center"></el-table-column>
        <el-table-column prop="finishedNum" label="FinishedNum" width="110" align="center"></el-table-column>
        <el-table-column prop="passedNum" label="PassedNum" width="100" align="center">
          <template #default="{ row }">
            <el-tag effect="dark" :type="getRunResultType(row)">
              {{ row.passedNum }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdOn" label="CreatedOn" width="160">
          <template #default="scope">
            {{ TimeUtils.displayTimeUptoSecs(scope.row.createdOn) }}
          </template>
        </el-table-column>
        <el-table-column prop="timestamp" label="EndedOn" width="160">
          <template #default="scope">
            {{ TimeUtils.displayTimeUptoSecs(scope.row.timestamp) }}
          </template>
        </el-table-column>
        <el-table-column label="DurationSecs" width="110">
          <template #default="scope">
            {{ TimeUtils.computeDurationSecs(scope.row.createdOn, scope.row.timestamp) }}
          </template>
        </el-table-column>
        <el-table-column prop="endReason" label="EndReason" width="100"></el-table-column>

        <el-table-column label="Actions" width="200" align="center">
          <template #default="{ $index, row }">
            <el-button
              @click="handleEditChildRun($index, row)">
              ChildRun
            </el-button>

            <el-button
              @click="handleRerun($index, row)">
              Rerun
            </el-button>
          </template>
        </el-table-column>

        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="p-4">
              <div class="my-1">
                runGuid: {{ row.runGuid }}
              </div>
              <div class="my-1">
                CreatedOn: {{ row.createdOn }}
              </div>
              <div class="my-1">
                EndedOn: {{ row.timestamp }}
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
          :total="totalJobRunResults"

        ></el-pagination>
      </div>
    </div>

    <el-dialog
      v-if="jobRunStartFormDialogVisible"
      v-model="jobRunStartFormDialogVisible"
      :title="jobRunStartFormTitle"
    >
      <JobRunStartForm
        :job-run-result="editJobRunResult"
        @ok="jobRunStartFormDialogVisible = false"
        @cancel="jobRunStartFormDialogVisible = false">
      </JobRunStartForm>
    </el-dialog>

    <el-dialog
      v-if="finishedJobChildRunDialogVisible"
      v-model="finishedJobChildRunDialogVisible"
      :title="finishedJobChildRunDialogTitle"
      fullscreen
    >
      <FinishedJobChildRunDialog
        :job-run="editJobRunResult!"
        @ok="finishedJobChildRunDialogVisible = false"
        @cancel="finishedJobChildRunDialogVisible = false">
      </FinishedJobChildRunDialog>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed, onBeforeUnmount  } from 'vue';

import { Search } from '@element-plus/icons-vue';

import JobRunStartForm from '@/components/jms/JobRunStartForm.vue';
import FinishedJobChildRunDialog from '@/components/jms/FinishedJobChildRunDialog.vue';

import type { JmsJobRunResultFull, JmsJobRunResultQueryParam } from "@/model/dto/jms";
import type { EmsEnvironmentBase } from '@/model/dto/ems';

import { gJobRunApi } from '@/api';
import { TimeUtils } from '@/utils';

import { useProjectStore } from '@/stores/project';
import { useEnvironmentStore } from '@/stores/environment';

const logPrefix = 'FinishedJobRunPanel:';

const props = defineProps({
  visible: {
    type: Boolean,
    required: true,
  }
});

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

    refreshJobRunResultList();
    refreshInterval = window.setInterval(refreshJobRunResultList, 10_000);
  } else {
    console.log(`${logPrefix} stop refresh interval`);

    clearInterval(refreshInterval);
    refreshInterval = undefined;
  }
}, { immediate: true })
//#endregion

//#region JobRunResult List
const loading = ref(false);
const templateName = ref('');
const runTag = ref('');
const environmentName = ref('all');
const selectedEnvironmentBase = ref<EmsEnvironmentBase>();
const pageNum = ref(1);
const pageSize = ref(10);
const jobRunResultList = ref([] as JmsJobRunResultFull[]);
const totalJobRunResults = ref(0);

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
  refreshJobRunResultList();
})

async function refreshJobRunResultList() {
  console.log(`${logPrefix} refreshJobRunResultList`);

  try {
    loading.value = true;

    const queryParam: JmsJobRunResultQueryParam = {
      projectId: projectStore.selectedProjectBase!.id,
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    };

    templateName.value && (queryParam.templateName = templateName.value);
    runTag.value && (queryParam.runTag = runTag.value);
    selectedEnvironmentBase.value?.id && (queryParam.environmentId = selectedEnvironmentBase.value.id);

    const rsp = await gJobRunApi.getJobRunResultList(queryParam);

    jobRunResultList.value = rsp.data?.list || [];
    totalJobRunResults.value = rsp.data?.total || 0;
  } finally {
    loading.value= false;
  }
}
//#endregion

//#region JobRunResult List Item
const jobRunStartFormDialogVisible = ref(false);
const jobRunStartFormTitle = ref('Rerun JobRun');
const finishedJobChildRunDialogVisible = ref(false);
const finishedJobChildRunDialogTitle = ref('');
const editJobRunResult = ref<JmsJobRunResultFull>();

watch([jobRunStartFormDialogVisible], ([value]) => {
  if (!value) {
    editJobRunResult.value = undefined;
  }
});

function handleRerun(index: number, row: JmsJobRunResultFull) {
  console.log(`Rerun: index ${index}, env ${row.environmentName}, template ${row.templateName}`);
  editJobRunResult.value = row;
  jobRunStartFormDialogVisible.value = true;
}

function handleEditChildRun(index: number, row: JmsJobRunResultFull) {
  console.log(`handleEditChildRun: index ${index}, env ${row.environmentName}, template ${row.templateName}`);
  editJobRunResult.value = row;
  finishedJobChildRunDialogTitle.value = `Finished JobChildRun of run: ${row.runGuid}`
  finishedJobChildRunDialogVisible.value = true;
}

function getRunResultType(row: JmsJobRunResultFull) {
  if (row.passed) {
    return 'success';
  }
  return 'danger';
}
//#endregion
</script>

<style lang="scss" scoped>
.panel-container {
  width: 100%;
  height: 100%;

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

  .red {
    color: red;
  }

  .pagination {
    margin-top: 10px;
    display: flex;
  }
}
</style>
