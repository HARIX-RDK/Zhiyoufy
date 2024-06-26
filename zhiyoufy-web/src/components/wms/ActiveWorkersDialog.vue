<template>
  <div class="view-container">
    <div class="toolbar">
      <span>Search</span>
      <el-input
        class="ml-1 keyword-input"
        v-model="keyword"
        placeholder="keyword"
      >
      </el-input>

      <el-button class="ml-4"
        :icon="Refresh" @click="refreshWorkerListAll">
        Refresh
      </el-button>
    </div>

    <el-table :data="workerListPage" border class="table" @sort-change="onSortChange">
      <el-table-column prop="workerName" label="WorkerName" min-width="300" sortable="custom"></el-table-column>
      <el-table-column prop="maxActiveJobNum" label="MaxActiveJobNum" width="170" sortable="custom"></el-table-column>
      <el-table-column prop="freeActiveJobNum" label="FreeActiveJobNum" width="170" sortable="custom"></el-table-column>
      <el-table-column prop="disconnected" label="Disconnected" width="140" sortable="custom"></el-table-column>
      <el-table-column prop="connectTime" label="ConnectTime" width="160" sortable="custom">
        <template #default="scope">
          {{ TimeUtils.displayTimeUptoSecs(scope.row.connectTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="disconnectTime" label="DisconnectTime" width="160" sortable="custom">
        <template #default="scope">
          {{ TimeUtils.displayTimeUptoSecs(scope.row.disconnectTime) }}
        </template>
      </el-table-column>

      <el-table-column label="Actions" width="180" align="center">
        <template #default="scope">
          <el-button
            type="warning" @click="handleDisconnectSession(scope.$index, scope.row)">
            Disconnect Session
          </el-button>
        </template>
      </el-table-column>

      <el-table-column type="expand">
        <template #default="{ row }">
          <div class="p-4">
            <div class="my-1">
              SessionId: {{ row.sessionId }}
            </div>
            <div class="my-1">
              AppRunId: {{ row.appRunId }}
            </div>
            <div class="my-1">
              AppStartTimestamp: {{ row.appStartTimestamp }}
            </div>
            <div class="my-1">
              WorkerLabels: {{ row.workerLabels }}
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
        v-model:current-page="table1PageNum"
        v-model:page-size="table1PageSize"
        :page-sizes="[10, 20, 30]"
        :total="totalWorkers"

      ></el-pagination>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onBeforeMount, computed } from 'vue';
import type { PropType } from 'vue';

import { Refresh } from '@element-plus/icons-vue';

import { gActiveWorkerApi } from '@/api';
import { TimeUtils } from '@/utils';

import type { WmsActiveWorkerGroupBase, WmsActiveWorkerBase } from "@/model/dto/wms";

const props = defineProps({
  workerGroup: {
    type: Object as PropType<WmsActiveWorkerGroupBase>,
    required: true,
  },
});

onBeforeMount(() => {
  refreshWorkerListAll();
})

//#region Worker List
const loading = ref(false);
const keyword = ref('');
const table1PageNum = ref(1);
const table1PageSize = ref(10);
const table1SortBy = ref('');
const table1SortDesc = ref<boolean | undefined>();
const workerListAll = ref<WmsActiveWorkerBase[]>([]);
const workerListProcessed = computed(() => {
  let targetWorkerList = [...workerListAll.value];

  if (keyword.value) {
    targetWorkerList = targetWorkerList.filter((item) => {
      if (item.workerName.includes(keyword.value)) {
        return true;
      }
      return false;
    })
  }

  if (table1SortBy.value) {
    targetWorkerList.sort((a, b) => {
      const prop = table1SortBy.value as keyof WmsActiveWorkerBase;
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

  return targetWorkerList;
});
const totalWorkers = computed(() => {
  return workerListProcessed.value.length;
});
const workerListPage = computed(() => {
  const start = (table1PageNum.value - 1) * table1PageSize.value;
  const end = table1PageNum.value * table1PageSize.value;
  let targetWorkerList = workerListProcessed.value.slice(start, end);
  return targetWorkerList;
})

async function refreshWorkerListAll() {
  console.log(`refreshWorkerListAll`);

  try {
    loading.value = true;

    const rsp = await gActiveWorkerApi.getWorkerBaseList(props.workerGroup.id);

    workerListAll.value = rsp.data || [];
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

async function handleDisconnectSession(index: number, row: WmsActiveWorkerBase) {
  console.log(`handleDisconnectSession: index ${index}, row ${row.workerName}`);

  await gActiveWorkerApi.disconnectSession(row.sessionId);

  await TimeUtils.sleep(3000);

  await refreshWorkerListAll();
}
</script>

<style lang="scss" scoped>
.view-container {
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

  .pagination {
    margin-top: 10px;
    display: flex;
  }
}
</style>
