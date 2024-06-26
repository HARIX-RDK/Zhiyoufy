<template>
  <div class="panel-container">
    <div class="toolbar">
      <span>Search</span>

      <el-input
        class="ml-1 keyword-input"
        v-model="keyword"
        placeholder="keyword"
      >
      </el-input>

      <el-checkbox v-if="userStore.isAdmin"
        class="ml-2"
        v-model="allUsers" label="All Users" />

      <el-button class="ml-4"
        :icon="Refresh" @click="refreshActiveJobRunBaseListAll">
        Refresh
      </el-button>
    </div>

    <el-table :data="activeJobRunBaseListPage" border class="table" @sort-change="onSortChange">
      <el-table-column prop="environmentName" label="Environment" sortable="custom"></el-table-column>
      <el-table-column prop="templateName" label="Template" sortable="custom"></el-table-column>
      <el-table-column prop="username" label="Username" sortable="custom" width="120"></el-table-column>
      <el-table-column prop="runTag" label="RunTag"></el-table-column>
      <el-table-column prop="runNum" label="RunNum" width="80"></el-table-column>
      <el-table-column prop="startedNum" label="StartedNum" width="100"></el-table-column>
      <el-table-column prop="activeNum" label="ActiveNum" width="100"></el-table-column>
      <el-table-column prop="finishedNum" label="FinishedNum" width="110"></el-table-column>
      <el-table-column prop="passedNum" label="PassedNum" width="100"></el-table-column>
      <el-table-column prop="createdOn" label="CreatedOn" width="160">
        <template #default="scope">
          {{ TimeUtils.displayTimeUptoSecs(scope.row.createdOn) }}
        </template>
      </el-table-column>
      <el-table-column label="DurationSecs" width="110">
        <template #default="scope">
          {{ TimeUtils.computeDurationSecs(scope.row.createdOn) }}
        </template>
      </el-table-column>

      <el-table-column label="Actions" width="180" align="center">
          <template #default="{ $index, row }">
            <el-button v-if="row.state === 'Running' && row.perf"
              text :icon="Edit" @click="handleEditPerf($index, row)">
              Perf
            </el-button>

            <el-button v-if="row.state === 'Running'"
              text class="red" @click="handleStop($index, row)">
              Stop
            </el-button>
          </template>
        </el-table-column>
    </el-table>

    <div class="pagination">
      <div class="spacer"></div>
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        v-model:current-page="table1PageNum"
        v-model:page-size="table1PageSize"
        :page-sizes="[10, 20, 30]"
        :total="totalActiveJobRunBases"

      ></el-pagination>
    </div>

    <el-dialog
      v-if="perfJobRunDialogVisible"
      v-model="perfJobRunDialogVisible"
      title="Perf JobRunDialog"
      fullscreen
    >
      <PerfJobRunDialog
        :jobRun="editPerfJobRun!">
      </PerfJobRunDialog>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onBeforeUnmount, computed, watch } from 'vue';

import { Refresh, Edit } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';

import PerfJobRunDialog from '@/components/jms/PerfJobRunDialog.vue';

import { gJobRunApi } from '@/api';
import { TimeUtils } from '@/utils';

import type { JmsActiveJobRunBase } from "@/model/dto/jms";

import { useUserStore } from '@/stores/user';

const props = defineProps({
  visible: {
    type: Boolean,
    required: true,
  }
});

const userStore = useUserStore();

const handling = ref(false);

//#region Auto Refresh
let refreshInterval: number | undefined;

onBeforeUnmount(() => {
  clearInterval(refreshInterval);
})

watch(() => props.visible, (value) => {
  if (value) {
    console.log(`start refresh interval`);

    refreshActiveJobRunBaseListAll();
    refreshInterval = window.setInterval(refreshActiveJobRunBaseListAll, 10_000);
  } else {
    console.log(`stop refresh interval`);

    clearInterval(refreshInterval);
    refreshInterval = undefined;
  }
}, { immediate: true })
//#endregion

//#region ActiveJobRunBase List
const allUsers = ref(false);
const loading = ref(false);
const keyword = ref('');
const table1PageNum = ref(1);
const table1PageSize = ref(10);
const table1SortBy = ref('');
const table1SortDesc = ref<boolean | undefined>();
const activeJobRunBaseListAll = ref<JmsActiveJobRunBase[]>([]);
const activeJobRunBaseListProcessed = computed(() => {
  let targetActiveJobRunBaseList = [...activeJobRunBaseListAll.value];

  if (keyword.value) {
    targetActiveJobRunBaseList = targetActiveJobRunBaseList.filter((item) => {
      if (item.runGuid.includes(keyword.value) || item.environmentName.includes(keyword.value)
        || item.templateName.includes(keyword.value)) {
        return true;
      }
      return false;
    })
  }

  if (table1SortBy.value) {
    targetActiveJobRunBaseList.sort((a, b) => {
      const prop = table1SortBy.value as keyof JmsActiveJobRunBase;
      if (a[prop] < b[prop]) {
        if (!table1SortDesc.value) {
          return -1;
        } else {
          return 1;
        }
      } else if (a[prop] > b[prop]) {
        if (!table1SortDesc.value) {
          return 1;
        } else {
          return -1;
        }
      } else {
        return 0;
      }
    })
  }

  return targetActiveJobRunBaseList;
});
const totalActiveJobRunBases = computed(() => {
  return activeJobRunBaseListProcessed.value.length;
});
const activeJobRunBaseListPage = computed(() => {
  const start = (table1PageNum.value - 1) * table1PageSize.value;
  const end = table1PageNum.value * table1PageSize.value;
  let targetActiveJobRunBaseList = activeJobRunBaseListProcessed.value.slice(start, end);
  return targetActiveJobRunBaseList;
})

async function refreshActiveJobRunBaseListAll() {
  console.log(`refreshActiveJobRunBaseListAll`);

  try {
    loading.value = true;

    const rsp = await gJobRunApi.getActiveJobRunBaseList(allUsers.value);

    activeJobRunBaseListAll.value = rsp.data || [];
  } finally {
    loading.value= false;
  }
}

async function onSortChange({ column, prop, order }: any) {
  if (!order) {
    table1SortDesc.value = undefined;
  } else if (order === 'ascending') {
    table1SortDesc.value = false;
  } else if (order === 'descending') {
    table1SortDesc.value = true;
  } else {
    throw new Error(`unexpected order ${order}`)
  }

  if (table1SortDesc.value == null) {
    table1SortBy.value = '';
  } else {
    if (prop) {
      table1SortBy.value = prop;
    } else {
      throw new Error(`unexpected column ${column.label}`);
    }
  }

  console.log(`table1SortBy ${table1SortBy.value}, table1SortDesc ${table1SortDesc.value}`);
}
//#endregion

//#region List Item
const perfJobRunDialogVisible = ref(false);
const editPerfJobRun = ref<JmsActiveJobRunBase>();

watch([perfJobRunDialogVisible], ([value]) => {
  if (!value) {
    editPerfJobRun.value = undefined;
  }
});

async function handleEditPerf(index: number, row: JmsActiveJobRunBase) {
  console.log(`handleEditPerf: index ${index}, row ${row.runGuid}`);

  editPerfJobRun.value = row;
  perfJobRunDialogVisible.value = true;
}

async function handleStop(index: number, row: JmsActiveJobRunBase) {
  console.log(`handleStop: index ${index}, row ${row.runGuid}`);

  try {
    handling.value = true;

    const action = await ElMessageBox.confirm(
      `Are you sure to stop jobRun template ${row.templateName}, runGuid ${row.runGuid}. Continue?`,
      'Warning',
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
    );

    if (action === 'confirm') {
      await gJobRunApi.stopJobRun(row.runGuid);

      await refreshActiveJobRunBaseListAll();

      await TimeUtils.sleep(2000);

      await refreshActiveJobRunBaseListAll();
    }
  }
  finally {
    handling.value = false;
  }
}
//#endregion
</script>

<style lang="scss" scoped>
.panel-container {
  padding: 10px;
  height: 100%;

  .toolbar {
    margin-bottom: 16px;
    display: flex;
    flex-wrap: wrap;
    align-items: center;

    .keyword-input {
      width: 350px;
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
