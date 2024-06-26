<template>
  <div class="dialog-container">
    <div class="dialog-content">
      <div class="toolbar">
        <span>Search</span>
        <el-input
          class="ml-1 keyword-input"
          v-model="keyword"
          placeholder="keyword"
        >
          <template #append>
            <el-button :icon="Search"
              @click="refreshWorkerGroupList"
              />
          </template>
        </el-input>

        <div class="spacer"></div>

        <el-button type="primary" :icon="Plus" @click="onNewWorkerGroup">
          New WorkerGroup
        </el-button>
      </div>

      <el-table :data="workerGroupList" border class="table" @sort-change="onSortChange">
        <el-table-column prop="name" label="Name" sortable="custom"></el-table-column>
        <el-table-column prop="modifiedBy" label="ModifiedBy"  width="200"></el-table-column>
        <el-table-column label="ModifiedTime" width="160" sortable="custom">
          <template #default="scope">
            {{ TimeUtils.displayTimeUptoSecs(scope.row.modifiedTime) }}
          </template>
        </el-table-column>

        <el-table-column label="Actions" width="400" align="center">
          <template #default="{ $index, row }">
            <el-button
              text :icon="Edit" @click="handleEditGroupTokens($index, row)">
              Group Tokens
            </el-button>

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
                CreatedTime: {{ row.createdTime }}
              </div>
              <div class="my-1">
                CreatedBy: {{ row.createdBy }}
              </div>
              <div class="my-1">
                ModifiedTime: {{ row.modifiedTime }}
              </div>
              <div class="my-1">
                ModifiedBy: {{ row.modifiedBy }}
              </div>
              <div class="my-1">
                WorkerLabels: {{ row.workerLabels }}
              </div>
              <div class="my-1">
                Description: {{ row.description }}
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
          :total="totalWorkerGroups"

        ></el-pagination>
      </div>
    </div>

    <el-dialog
      v-if="workerGroupFormDialogVisible"
      v-model="workerGroupFormDialogVisible"
      :title="workerGroupFormTitle"
    >
      <WorkerGroupForm
        :worker-app="workerApp"
        :worker-group="editWorkerGroup"
        @ok="onWorkerGroupFormDialogOk"
        @cancel="workerGroupFormDialogVisible = false">
      </WorkerGroupForm>
    </el-dialog>

    <el-dialog
      v-if="groupTokensDialogVisible"
      v-model="groupTokensDialogVisible"
      :title="groupTokensDialogTitle"
      fullscreen>
      <GroupTokensDialog
        :workerApp="workerApp"
        :worker-group="editWorkerGroup!"
      >
      </GroupTokensDialog>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onBeforeMount  } from 'vue';
import type { PropType } from 'vue';

import { Delete, Edit, Search, Plus } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';

import WorkerGroupForm from '@/components/wms/WorkerGroupForm.vue';
import GroupTokensDialog from '@/components/wms/GroupTokensDialog.vue';

import type { WmsWorkerApp, WmsWorkerGroup, WmsWorkerGroupQueryParam } from "@/model/dto/wms";

import { gWorkerGroupApi } from '@/api';
import { TimeUtils } from '@/utils';

const props = defineProps({
  workerApp: {
    type: Object as PropType<WmsWorkerApp>,
    required: true,
  },
});

const handling = ref(false);

//#region Worker Group List
const loading = ref(false);
const keyword = ref('');
const pageNum = ref(1);
const pageSize = ref(10);
const workerGroupList = ref([] as WmsWorkerGroup[]);
const totalWorkerGroups = ref(0);
let sortBy = '';
let sortDesc: boolean | undefined = undefined;

onBeforeMount(() => {
  refreshWorkerGroupList();
});

watch([pageNum, pageSize], () => {
  refreshWorkerGroupList();
})

async function refreshWorkerGroupList() {
  console.log(`refreshWorkerGroupList`);

  try {
    loading.value = true;

    const queryParam: WmsWorkerGroupQueryParam = {
      workerAppId: props.workerApp.id,
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    };

    keyword.value && (queryParam.keyword = keyword.value);

    if (sortBy) {
      queryParam.sortBy = sortBy;
      queryParam.sortDesc = sortDesc;
    }

    const rsp = await gWorkerGroupApi.getWorkerGroupList(queryParam);

    workerGroupList.value = rsp.data?.list || [];
    totalWorkerGroups.value = rsp.data?.total || 0;
  } finally {
    loading.value= false;
  }
}

async function onSortChange({ column, prop, order }: any) {
  if (!order) {
    sortDesc = undefined;
  } else if (order === 'ascending') {
    sortDesc = false;
  } else if (order === 'descending') {
    sortDesc = true;
  } else {
    throw new Error(`unexpected order ${order}`)
  }

  if (sortDesc == null) {
    sortBy = '';
  } else {
    if (prop) {
      sortBy = prop;
    } else if (column.label === 'ModifiedTime') {
      sortBy = 'modified_time';
    } else {
      throw new Error(`unexpected column ${column.label}`);
    }
  }

  console.log(`sortBy ${sortBy}, sortDesc ${sortDesc}`);

  await refreshWorkerGroupList();
}
//#endregion

//#region Worker Group List Item
async function handleDelete(index: number, row: WmsWorkerGroup) {
  console.log(`handleDelete: index ${index}, row ${row.name}`);

  try {
    handling.value = true;

    const action = await ElMessageBox.confirm(
      `Are you sure to delete worker group ${row.name}. Continue?`,
      'Warning',
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
    );

    if (action === 'confirm') {
      await gWorkerGroupApi.delWorkerGroup(row.id);

      await refreshWorkerGroupList();
    }
  }
  finally {
    handling.value = false;
  }
}
//#endregion

//#region Group Tokens
const groupTokensDialogVisible = ref(false);
const groupTokensDialogTitle = ref('');

async function handleEditGroupTokens(index: number, row: WmsWorkerGroup) {
  console.log(`handleEditGroupTokens: index ${index}, row ${row.name}`);
  editWorkerGroup.value = row;
  groupTokensDialogTitle.value = `GroupTokens of WorkerGroup: ${editWorkerGroup.value.name}`;
  groupTokensDialogVisible.value = true;
}
//#endregion

//#region Worker Group Form
const workerGroupFormDialogVisible = ref(false);
const workerGroupFormTitle = ref('');
const editWorkerGroup = ref<WmsWorkerGroup>();

watch([workerGroupFormDialogVisible], ([value]) => {
  if (!value) {
    editWorkerGroup.value = undefined;
  }
});

async function handleEdit(index: number, row: WmsWorkerGroup) {
  console.log(`handleEdit: index ${index}, row ${row.name}`);
  editWorkerGroup.value = row;
  workerGroupFormTitle.value = `Edit WorkerGroup ${editWorkerGroup.value.name}`;
  workerGroupFormDialogVisible.value = true;
}

function onNewWorkerGroup() {
  workerGroupFormTitle.value = 'New WorkerGroup';
  workerGroupFormDialogVisible.value = true;
}

function onWorkerGroupFormDialogOk() {
  workerGroupFormDialogVisible.value = false;
  refreshWorkerGroupList();
}
//#endregion
</script>

<style lang="scss" scoped>
.dialog-container {
  width: 100%;
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
