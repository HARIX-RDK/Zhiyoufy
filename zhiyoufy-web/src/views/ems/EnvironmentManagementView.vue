<template>
  <div class="env-management-container">
    <div class="toolbar">
      <span>Search</span>
      <el-input
        class="ml-1 keyword-input"
        v-model="keyword"
        placeholder="keyword"
      >
        <template #append>
          <el-button :icon="Search"
            @click="refreshEnvironmentList"
            />
        </template>
      </el-input>

      <el-checkbox v-if="userStore.isAdmin"
        class="ml-2"
        v-model="allUsers" label="All Users" />

      <div class="spacer"></div>

      <el-button
        v-if="hasEnvironmentOwnerRole()"
        type="primary" :icon="Plus" @click="onNewEnvironment">New Environment</el-button>
    </div>

    <el-table :data="environmentList" border class="table" @sort-change="onSortChange">
      <el-table-column prop="name" label="Name" sortable="custom"></el-table-column>
      <el-table-column prop="description" label="Description" width="400"
        show-overflow-tooltip></el-table-column>

      <el-table-column label="Actions" width="400" align="center">
        <template #default="scope">
          <el-button
            text :icon="Edit" @click="handleEditConfigCollections(scope.$index, scope.row)">
            Collections
          </el-button>

          <el-button
            text :icon="Edit" @click="handleEditConfigSingles(scope.$index, scope.row)">
            Singles
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
              Parent: {{ getEnvParent(row) }}
            </div>
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
              ExtraArgs: {{ row.extraArgs }}
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
        :total="totalEnvironments"

      ></el-pagination>
    </div>

    <el-dialog
      v-if="envFormDialogVisible"
      v-model="envFormDialogVisible"
      :title="envFormDialogTitle"
    >
      <EnvironmentForm
        :environment="editEnvironment"
        :environment-base-list="environmentBaseList"
        @ok="onEnvFormDialogOk" @cancel="envFormDialogVisible = false">
      </EnvironmentForm>
    </el-dialog>

    <el-dialog
      v-if="configCollectionDialogVisible"
      v-model="configCollectionDialogVisible"
      :title="configCollectionDialogTitle"
      fullscreen>
      <EnvironmentConfigCollectionsDialog
        :environment="editEnvironment!"
      >
      </EnvironmentConfigCollectionsDialog>
    </el-dialog>

    <el-dialog
      v-if="configSingleDialogVisible"
      v-model="configSingleDialogVisible"
      :title="configSingleDialogTitle"
      fullscreen>
      <EnvironmentConfigSinglesDialog
        :environment="editEnvironment!"
      >
      </EnvironmentConfigSinglesDialog>
    </el-dialog>

    <el-dialog
      v-if="environmentUserDialogVisible"
      v-model="environmentUserDialogVisible"
      :title="environmentUserDialogTitle"
      fullscreen>
      <EnvironmentUserDialog
        :environment="editEnvironment!"
      >
      </EnvironmentUserDialog>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onBeforeMount } from 'vue';

import { Delete, Edit, Search, Plus } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';

import EnvironmentForm from '@/components/ems/EnvironmentForm.vue';
import EnvironmentConfigCollectionsDialog from '@/components/ems/EnvironmentConfigCollectionsDialog.vue';
import EnvironmentConfigSinglesDialog from '@/components/ems/EnvironmentConfigSinglesDialog.vue';
import EnvironmentUserDialog from '@/components/ems/EnvironmentUserDialog.vue';

import { useUserStore } from '@/stores/user';
import { gEnvironmentApi } from '@/api';
import { PermissionUtils } from '@/utils';

import type { EmsEnvironmentFull, EmsEnvironmentQueryParam, EmsEnvironmentBase } from '@/model/dto/ems';


const userStore = useUserStore();

const handling = ref(false);

onBeforeMount(() => {
  refreshData();
})

async function refreshData() {
  await refreshEnvironmentBaseList();
  await refreshEnvironmentList();
}

//#region Environment Base List
const environmentBaseList = ref([] as EmsEnvironmentBase[]);

async function refreshEnvironmentBaseList() {
  try {
    loading.value = true;

    const rsp = await gEnvironmentApi.getEnvironmentBaseList();

    environmentBaseList.value = rsp.data || [];
  } finally {
    loading.value = false;
  }
}
//#endregion

//#region Environment List
const allUsers = ref(false);
const loading = ref(false);
const keyword = ref('');
const pageNum = ref(1);
const pageSize = ref(10);
const environmentList = ref([] as EmsEnvironmentFull[]);
const totalEnvironments = ref(0);

let sortBy = '';
let sortDesc: boolean | undefined = undefined;

watch([pageNum, pageSize], () => {
  refreshEnvironmentList();
})

async function refreshEnvironmentList() {
  console.log(`refreshEnvironmentList`);

  try {
    loading.value = true;

    const queryParam: EmsEnvironmentQueryParam = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    };

    keyword.value && (queryParam.keyword = keyword.value);
    allUsers.value && (queryParam.allUsers = allUsers.value);

    if (sortBy) {
      queryParam.sortBy = sortBy;
      queryParam.sortDesc = sortDesc;
    }

    const rsp = await gEnvironmentApi.getEnvironmentList(queryParam);

    environmentList.value = rsp.data?.list || [];
    totalEnvironments.value = rsp.data?.total || 0;
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

  await refreshEnvironmentList();
}
//#endregion

//#region Environment List Item
function getEnvParent(item: EmsEnvironmentFull) {
  if (!item.parentId) {
    return '';
  }

  const found = environmentBaseList.value.find(env => env.id === item.parentId);

  if (found) {
    return found.name;
  }

  return 'Unknown';
}

async function handleDelete(index: number, row: EmsEnvironmentFull) {
  console.log(`handleDelete: index ${index}, row ${row.name}`);

  try {
    handling.value = true;

    const action = await ElMessageBox.confirm(
      `Are you sure to delete environment ${row.name}. Continue?`,
      'Warning',
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
    );

    if (action === 'confirm') {
      await gEnvironmentApi.delEnvironment(row.id);

      await refreshData();
    }
  }
  finally {
    handling.value = false;
  }
}

function hasEnvironmentOwnerRole() {
  return PermissionUtils.hasRole('environment.owner');
}

function isEditor(item: EmsEnvironmentFull) {
  return allUsers.value || item.isOwner || item.isEditor;
}

function isOwner(item: EmsEnvironmentFull) {
  return allUsers.value || item.isOwner;
}
//#endregion

//#region Environment Form
const envFormDialogVisible = ref(false);
const envFormDialogTitle = ref('');
const editEnvironment = ref<EmsEnvironmentFull>();

watch([envFormDialogVisible], ([value]) => {
  if (!value) {
    editEnvironment.value = undefined;
  }
});

async function handleEdit(index: number, row: EmsEnvironmentFull) {
  console.log(`handleEdit: index ${index}, row ${row.name}`);
  editEnvironment.value = row;
  envFormDialogTitle.value = 'Edit Environment';
  envFormDialogVisible.value = true;
}

function onNewEnvironment() {
  envFormDialogTitle.value = 'New Environment';
  envFormDialogVisible.value = true;
}

function onEnvFormDialogOk() {
  envFormDialogVisible.value = false;
  refreshData();
}
//#endregion

//#region Config Collections
const configCollectionDialogVisible = ref(false);
const configCollectionDialogTitle = ref('');

async function handleEditConfigCollections(index: number, row: EmsEnvironmentFull) {
  console.log(`handleEditConfigCollections: index ${index}, row ${row.name}`);
  editEnvironment.value = row;
  configCollectionDialogTitle.value = `Environment Config Collections For ${editEnvironment.value.name}`;
  configCollectionDialogVisible.value = true;
}
//#endregion

//#region Config Singles
const configSingleDialogVisible = ref(false);
const configSingleDialogTitle = ref('');

async function handleEditConfigSingles(index: number, row: EmsEnvironmentFull) {
  console.log(`handleEditConfigSingles: index ${index}, row ${row.name}`);
  editEnvironment.value = row;
  configSingleDialogTitle.value = `Environment Config Singles For ${editEnvironment.value.name}`;
  configSingleDialogVisible.value = true;
}
//#endregion

//#region Environment User
const environmentUserDialogVisible = ref(false);
const environmentUserDialogTitle = ref('');

async function handleEditUsers(index: number, row: EmsEnvironmentFull) {
  console.log(`handleEditUsers: index ${index}, row ${row.name}`);
  editEnvironment.value = row;
  environmentUserDialogTitle.value = `Environment Users For ${editEnvironment.value.name}`;
  environmentUserDialogVisible.value = true;
}
//#endregion
</script>

<style lang="scss" scoped>
.env-management-container {
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
