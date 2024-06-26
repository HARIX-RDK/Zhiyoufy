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
        :icon="Refresh" @click="refreshAppBaseListAll">
        Refresh
      </el-button>
    </div>

    <el-table :data="appBaseListPage" border class="table" @sort-change="onSortChange">
      <el-table-column prop="name" label="Name" sortable="custom"></el-table-column>

      <el-table-column label="Actions" width="200" align="center">
        <template #default="scope">
          <el-button
            text :icon="Edit" @click="handleEditWorkerGroups(scope.$index, scope.row)">
            Worker Groups
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
        :total="totalAppBases"

      ></el-pagination>
    </div>

    <el-dialog
      v-if="workerGroupDialogVisible"
      v-model="workerGroupDialogVisible"
      :title="workerGroupDialogTitle"
      fullscreen>
      <ActiveWorkerGroupsDialog
        :app-base="editAppBase!"
      >
      </ActiveWorkerGroupsDialog>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onBeforeMount, computed } from 'vue';

import { Edit, Refresh } from '@element-plus/icons-vue';

import ActiveWorkerGroupsDialog from '@/components/wms/ActiveWorkerGroupsDialog.vue';

import { gActiveWorkerApi } from '@/api';

import type { WmsWorkerAppBase } from "@/model/dto/wms";


onBeforeMount(() => {
  refreshAppBaseListAll();
})

//#region AppBase List
const loading = ref(false);
const keyword = ref('');
const table1PageNum = ref(1);
const table1PageSize = ref(10);
const table1SortBy = ref('');
const table1SortDesc = ref<boolean | undefined>();
const appBaseListAll = ref<WmsWorkerAppBase[]>([]);
const appBaseListProcessed = computed(() => {
  let targetAppBaseList = [...appBaseListAll.value];

  if (keyword.value) {
    targetAppBaseList = targetAppBaseList.filter((item) => {
      if (item.name.includes(keyword.value)) {
        return true;
      }
      return false;
    })
  }

  if (table1SortBy.value) {
    targetAppBaseList.sort((a, b) => {
      const prop = table1SortBy.value as 'name';
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

  return targetAppBaseList;
});
const totalAppBases = computed(() => {
  return appBaseListProcessed.value.length;
});
const appBaseListPage = computed(() => {
  const start = (table1PageNum.value - 1) * table1PageSize.value;
  const end = table1PageNum.value * table1PageSize.value;
  let targetAppBaseList = appBaseListProcessed.value.slice(start, end);
  return targetAppBaseList;
})

async function refreshAppBaseListAll() {
  console.log(`refreshAppBaseListAll`);

  try {
    loading.value = true;

    const rsp = await gActiveWorkerApi.getAppBaseList();

    appBaseListAll.value = rsp.data || [];
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

//#region Worker Group
const workerGroupDialogVisible = ref(false);
const workerGroupDialogTitle = ref('');
const editAppBase = ref<WmsWorkerAppBase>();

async function handleEditWorkerGroups(index: number, row: WmsWorkerAppBase) {
  console.log(`handleEditWorkerGroups: index ${index}, row ${row.name}`);
  editAppBase.value = row;
  workerGroupDialogTitle.value = `Worker Groups of app:  ${editAppBase.value.name}`;
  workerGroupDialogVisible.value = true;
}
//#endregion
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
