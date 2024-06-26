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
        :icon="Refresh" @click="refreshWorkerGroupListAll">
        Refresh
      </el-button>
    </div>

    <el-table :data="workerGroupListPage" border @sort-change="onSortChange">
      <el-table-column prop="name" label="Name" min-width="300" sortable="custom"></el-table-column>
      <el-table-column prop="maxActiveJobNum" label="MaxActiveJobNum" sortable="custom"></el-table-column>
      <el-table-column prop="freeActiveJobNum" label="FreeActiveJobNum" sortable="custom"></el-table-column>

      <el-table-column label="Actions" width="200" align="center">
        <template #default="scope">
          <el-button
            text :icon="Edit" @click="handleEditWorkers(scope.$index, scope.row)">
            Workers
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
        :total="totalWorkerGroups"

      ></el-pagination>
    </div>

    <el-dialog
      v-if="workersDialogVisible"
      v-model="workersDialogVisible"
      :title="workersDialogTitle"
      fullscreen>
      <ActiveWorkersDialog
        :worker-group="editWorkerGroup!"
      >
      </ActiveWorkersDialog>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onBeforeMount, computed } from 'vue';
import type { PropType } from 'vue';

import { Edit, Refresh } from '@element-plus/icons-vue';

import ActiveWorkersDialog from '@/components/wms/ActiveWorkersDialog.vue';

import { gActiveWorkerApi } from '@/api';

import type { WmsWorkerAppBase, WmsActiveWorkerGroupBase } from "@/model/dto/wms";

const props = defineProps({
  appBase: {
    type: Object as PropType<WmsWorkerAppBase>,
    required: true,
  },
});

onBeforeMount(() => {
  refreshWorkerGroupListAll();
})

//#region WorkerGroup List
const loading = ref(false);
const keyword = ref('');
const table1PageNum = ref(1);
const table1PageSize = ref(10);
const table1SortBy = ref('');
const table1SortDesc = ref<boolean | undefined>();
const workerGroupListAll = ref<WmsActiveWorkerGroupBase[]>([]);
const workerGroupListProcessed = computed(() => {
  let targetWorkerGroupList = [...workerGroupListAll.value];

  if (keyword.value) {
    targetWorkerGroupList = targetWorkerGroupList.filter((item) => {
      if (item.name.includes(keyword.value)) {
        return true;
      }
      return false;
    })
  }

  if (table1SortBy.value) {
    targetWorkerGroupList.sort((a, b) => {
      const prop = table1SortBy.value as 'name' | 'maxActiveJobNum' | 'freeActiveJobNum';
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

  return targetWorkerGroupList;
});
const totalWorkerGroups = computed(() => {
  return workerGroupListProcessed.value.length;
});
const workerGroupListPage = computed(() => {
  const start = (table1PageNum.value - 1) * table1PageSize.value;
  const end = table1PageNum.value * table1PageSize.value;
  let targetWorkerGroupList = workerGroupListProcessed.value.slice(start, end);
  return targetWorkerGroupList;
})

async function refreshWorkerGroupListAll() {
  console.log(`refreshWorkerGroupListAll`);

  try {
    loading.value = true;

    const rsp = await gActiveWorkerApi.getGroupBaseList(props.appBase.id);

    workerGroupListAll.value = rsp.data || [];
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

//#region Workers Dialog
const workersDialogVisible = ref(false);
const workersDialogTitle = ref('');
const editWorkerGroup = ref<WmsActiveWorkerGroupBase>();

async function handleEditWorkers(index: number, row: WmsActiveWorkerGroupBase) {
  console.log(`handleEditWorkers: index ${index}, row ${row.name}`);
  editWorkerGroup.value = row;
  workersDialogTitle.value = `Workers of group:  ${editWorkerGroup.value.name}`;
  workersDialogVisible.value = true;
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
