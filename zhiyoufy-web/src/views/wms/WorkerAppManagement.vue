<template>
  <div class="view-container">
    <div class="toolbar">
      <span>Search</span>
      <el-input
        class="ml-1 keyword-input"
        v-model="keyword"
        placeholder="keyword"
      >
        <template #append>
          <el-button :icon="Search"
            @click="refreshWorkerAppList"
            />
        </template>
      </el-input>

      <el-checkbox v-if="userStore.isAdmin"
        class="ml-2"
        v-model="allUsers" label="All Users" />

      <div class="spacer"></div>

      <el-button
        v-if="hasWorkerAppOwnerRole()"
        type="primary" :icon="Plus" @click="onNewWorkerApp">New WorkerApp
      </el-button>
    </div>

    <el-table :data="workerAppList" border class="table" @sort-change="onSortChange">
      <el-table-column prop="name" label="Name" sortable="custom"></el-table-column>
      <el-table-column prop="description" label="Description" width="400"
        show-overflow-tooltip></el-table-column>

      <el-table-column label="Actions" width="260" align="center">
        <template #default="scope">
          <el-button
            text :icon="Edit" @click="handleEditWorkerGroups(scope.$index, scope.row)">
            Worker Groups
          </el-button>

          <el-button v-if="isEditor(scope.row)"
            text :icon="Edit" @click="handleEdit(scope.$index, scope.row)">
            Edit
          </el-button>
        </template>
      </el-table-column>

      <el-table-column type="expand">
        <template #default="{ $index, row }">
          <div class="p-4">
            <span>More Actions: </span>
            <el-button v-if="isOwner(row)"
              text :icon="Edit" @click="handleEditUsers($index, row)">
              Users
            </el-button>

            <el-button v-if="isOwner(row)"
              text :icon="Delete" class="red" @click="handleDelete($index, row)">
              Delete
            </el-button>

            <hr>

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
              Description: {{ row.description }}
            </div>
            <div class="my-1">
              WorkerLabels: {{ row.workerLabels }}
            </div>
            <div class="my-1">
              NeedConfigBeJson: {{ row.needConfigBeJson }}
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
        :total="totalWorkerApps"

      ></el-pagination>
    </div>

    <el-dialog
      v-if="workerAppFormDialogVisible"
      v-model="workerAppFormDialogVisible"
      :title="workerAppFormDialogTitle"
    >
      <WorkerAppForm
        :workerApp="editWorkerApp"
        @ok="onEnvFormDialogOk" @cancel="workerAppFormDialogVisible = false">
      </WorkerAppForm>
    </el-dialog>

    <el-dialog
      v-if="workerGroupsDialogVisible"
      v-model="workerGroupsDialogVisible"
      :title="workerGroupsDialogTitle"
      fullscreen>
      <WorkerGroupsDialog
        :workerApp="editWorkerApp!"
      >
      </WorkerGroupsDialog>
    </el-dialog>

    <el-dialog
      v-if="workerAppUsersDialogVisible"
      v-model="workerAppUsersDialogVisible"
      :title="workerAppUsersDialogTitle"
      fullscreen>
      <WorkerAppUsersDialog
        :workerApp="editWorkerApp!"
      >
      </WorkerAppUsersDialog>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onBeforeMount } from 'vue';

import { Delete, Edit, Search, Plus } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';

import WorkerAppForm from '@/components/wms/WorkerAppForm.vue';
import WorkerGroupsDialog from '@/components/wms/WorkerGroupsDialog.vue';
import WorkerAppUsersDialog from '@/components/wms/WorkerAppUsersDialog.vue';

import { useUserStore } from '@/stores/user';
import { gWorkerAppApi } from '@/api';
import { PermissionUtils } from '@/utils';

import type { WmsWorkerAppFull, WmsWorkerAppQueryParam } from '@/model/dto/wms';


const userStore = useUserStore();

const handling = ref(false);

onBeforeMount(() => {
  refreshWorkerAppList();
})

//#region WorkerApp List
const allUsers = ref(false);
const loading = ref(false);
const keyword = ref('');
const table1PageNum = ref(1);
const table1PageSize = ref(10);
const workerAppList = ref([] as WmsWorkerAppFull[]);
const totalWorkerApps = ref(0);

let sortBy = '';
let sortDesc: boolean | undefined = undefined;

watch([table1PageNum, table1PageSize], () => {
  refreshWorkerAppList();
})

async function refreshWorkerAppList() {
  console.log(`refreshWorkerAppList`);

  try {
    loading.value = true;

    const queryParam: WmsWorkerAppQueryParam = {
      pageNum: table1PageNum.value,
      pageSize: table1PageSize.value,
    };

    keyword.value && (queryParam.keyword = keyword.value);
    allUsers.value && (queryParam.allUsers = allUsers.value);

    if (sortBy) {
      queryParam.sortBy = sortBy;
      queryParam.sortDesc = sortDesc;
    }

    const rsp = await gWorkerAppApi.getWorkerAppList(queryParam);

    workerAppList.value = rsp.data?.list || [];
    totalWorkerApps.value = rsp.data?.total || 0;
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
    } else {
      throw new Error(`unexpected column ${column.label}`);
    }
  }

  console.log(`sortBy ${sortBy}, sortDesc ${sortDesc}`);

  await refreshWorkerAppList();
}
//#endregion

//#region WorkerApp List Item
async function handleDelete(index: number, row: WmsWorkerAppFull) {
  console.log(`handleDelete: index ${index}, row ${row.name}`);

  try {
    handling.value = true;

    const action = await ElMessageBox.confirm(
      `Are you sure to delete workerApp ${row.name}. Continue?`,
      'Warning',
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
    );

    if (action === 'confirm') {
      await gWorkerAppApi.delWorkerApp(row.id);

      await refreshWorkerAppList();
    }
  }
  finally {
    handling.value = false;
  }
}

function hasWorkerAppOwnerRole() {
  return PermissionUtils.hasRole('workerApp.owner');
}

function isEditor(item: WmsWorkerAppFull) {
  return allUsers.value || item.isOwner || item.isEditor;
}

function isOwner(item: WmsWorkerAppFull) {
  return allUsers.value || item.isOwner;
}
//#endregion

//#region WorkerApp Form
const workerAppFormDialogVisible = ref(false);
const workerAppFormDialogTitle = ref('');
const editWorkerApp = ref<WmsWorkerAppFull>();

watch([workerAppFormDialogVisible], ([value]) => {
  if (!value) {
    editWorkerApp.value = undefined;
  }
});

async function handleEdit(index: number, row: WmsWorkerAppFull) {
  console.log(`handleEdit: index ${index}, row ${row.name}`);
  editWorkerApp.value = row;
  workerAppFormDialogTitle.value = `Edit WorkerApp: ${row.name}`;
  workerAppFormDialogVisible.value = true;
}

function onNewWorkerApp() {
  workerAppFormDialogTitle.value = 'New WorkerApp';
  workerAppFormDialogVisible.value = true;
}

function onEnvFormDialogOk() {
  workerAppFormDialogVisible.value = false;
  refreshWorkerAppList();
}
//#endregion

//#region Worker Groups
const workerGroupsDialogVisible = ref(false);
const workerGroupsDialogTitle = ref('');

async function handleEditWorkerGroups(index: number, row: WmsWorkerAppFull) {
  console.log(`handleEditWorkerGroups: index ${index}, row ${row.name}`);
  editWorkerApp.value = row;
  workerGroupsDialogTitle.value = `Worker Groups of WorkerApp: ${editWorkerApp.value.name}`;
  workerGroupsDialogVisible.value = true;
}
//#endregion

//#region WorkerApp Users
const workerAppUsersDialogVisible = ref(false);
const workerAppUsersDialogTitle = ref('');

async function handleEditUsers(index: number, row: WmsWorkerAppFull) {
  console.log(`handleEditUsers: index ${index}, row ${row.name}`);
  editWorkerApp.value = row;
  workerAppUsersDialogTitle.value = `Users of WorkerApp: ${editWorkerApp.value.name}`;
  workerAppUsersDialogVisible.value = true;
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

  .red {
    color: red;
  }

  .pagination {
    margin-top: 10px;
    display: flex;
  }
}
</style>
