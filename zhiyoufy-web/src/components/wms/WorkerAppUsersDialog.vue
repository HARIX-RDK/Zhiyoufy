<template>
  <el-card class="container">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="WorkerApp Users" name="WorkerApp Users">
        <div>
          <el-table :data="workerAppUserListPage" border>
            <el-table-column prop="username" label="Username"></el-table-column>
            <el-table-column prop="isOwner" label="IsOwner"></el-table-column>
            <el-table-column prop="isEditor" label="IsEditor"></el-table-column>

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
          </el-table>

          <div class="pagination">
            <div class="spacer"></div>
            <el-pagination
              background
              layout="total, sizes, prev, pager, next, jumper"
              v-model:current-page="workerAppUsersPageNum"
              v-model:page-size="workerAppUsersPageSize"
              :page-sizes="[10, 20, 30]"
              :total="totalWorkerAppUsers"

            ></el-pagination>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="All Users" name="All Users">
        <div>
          <div class="toolbar">
            <span>Search</span>
            <el-input
              class="ml-1 keyword-input"
              v-model="keyword"
              placeholder="keyword"
            >
              <template #append>
                <el-button :icon="Search"
                  @click="refreshAllUserList"
                  />
              </template>
            </el-input>
          </div>

          <el-table :data="allUserList" border @sort-change="onSortChange">
            <el-table-column prop="username" label="Username" sortable="custom"></el-table-column>
            <el-table-column label="Actions" width="120" align="center">
              <template #default="scope">
                <el-button v-if="!hasItem(scope.row)"
                  text :icon="Plus" @click="onNewWorkerAppUserRelation(scope.row)">
                  Add
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination">
            <div class="spacer"></div>
            <el-pagination
              background
              layout="total, sizes, prev, pager, next, jumper"
              v-model:current-page="allUsersPageNum"
              v-model:page-size="allUsersPageSize"
              :page-sizes="[10, 20, 30]"
              :total="totalAllUsers"

            ></el-pagination>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog
      v-if="workerAppUserRelationDialogVisible"
      v-model="workerAppUserRelationDialogVisible"
      :title="workerAppUserRelationDialogTitle"
    >
      <WorkerAppUserRelationDialog
        :workerApp="workerApp"
        :workerApp-user-relation="editWorkerAppUserRelation"
        :user-base="targetWorkerAppUser"
        @ok="onWorkerAppUserRelationDialogOk"
        @cancel="workerAppUserRelationDialogVisible = false">
      </WorkerAppUserRelationDialog>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { ref, watch, onBeforeMount, computed } from 'vue';
import type { PropType } from 'vue';

import { Delete, Edit, Search, Plus } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';

import WorkerAppUserRelationDialog from '@/components/wms/WorkerAppUserRelationDialog.vue';

import { gUserApi, gWorkerAppApi } from '@/api';

import type { PageQueryParam } from "@/model/dto/common";
import type { UmsUserBase, UmsUserQueryParam } from '@/model/dto/ums';
import type { WmsWorkerApp, WmsWorkerAppUserRelationFull } from "@/model/dto/wms";

const props = defineProps({
  workerApp: {
    type: Object as PropType<WmsWorkerApp>,
    required: true,
  },
});

const activeTab = ref('WorkerApp Users');

const handling = ref(false);

//#region WorkerApp Users List
onBeforeMount(() => {
  refreshWorkerAppUserListAll();
})

const workerAppUsersLoading = ref(false);
const workerAppUsersPageNum = ref(1);
const workerAppUsersPageSize = ref(10);
const workerAppUserListAll = ref<WmsWorkerAppUserRelationFull[]>([]);
const totalWorkerAppUsers = computed(() => {
  return workerAppUserListAll.value.length;
});
const workerAppUserListPage = computed(() => {
  const start = (workerAppUsersPageNum.value - 1) * workerAppUsersPageSize.value;
  const end = workerAppUsersPageNum.value * workerAppUsersPageSize.value;
  let targetWorkerAppUserList = workerAppUserListAll.value.slice(start, end);
  return targetWorkerAppUserList;
})

async function refreshWorkerAppUserListAll() {
  try {
    workerAppUsersLoading.value = true;

    const params: PageQueryParam = {
      pageNum: 1,
      pageSize: 10000,
    };

    const rsp = await gWorkerAppApi.getWorkerAppUserList({workerAppId: props.workerApp.id, params});

    workerAppUserListAll.value = rsp.data?.list || [];
  } finally {
    workerAppUsersLoading.value = false;
  }
}
//#endregion

//#region WorkerApp Users Item
async function handleDelete(index: number, row: WmsWorkerAppUserRelationFull) {
  console.log(`handleDelete: index ${index}, row ${row.username}`);

  try {
    handling.value = true;

    const action = await ElMessageBox.confirm(
      `Are you sure to delete user relation for ${row.username}. Continue?`,
      'Warning',
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
    );

    if (action === 'confirm') {
      await gWorkerAppApi.delWorkerAppUser(row.id);

      await refreshWorkerAppUserListAll();
    }
  }
  finally {
    handling.value = false;
  }
}
//#endregion

//#region All Users list
const keyword = ref('');
const allUsersPageNum = ref(1);
const allUsersPageSize = ref(10);
let sortBy = '';
let sortDesc: boolean | undefined = undefined;

const allUsersLoading = ref(false);
const allUserList = ref<UmsUserBase[]>([]);
const totalAllUsers = ref(0);

onBeforeMount(() => {
  refreshAllUserList();
});

watch([allUsersPageNum, allUsersPageSize], () => {
  refreshAllUserList();
})

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

  await refreshAllUserList();
}

async function refreshAllUserList() {
  try {
    allUsersLoading.value = true;

    const queryParam: UmsUserQueryParam = {
      pageNum: allUsersPageNum.value,
      pageSize: allUsersPageSize.value,
    };

    keyword.value && (queryParam.keyword = keyword.value);

    if (sortBy) {
      queryParam.sortBy = sortBy;
      queryParam.sortDesc = sortDesc;
    }

    const rsp = await gUserApi.getUserBaseList(queryParam);

    allUserList.value = rsp.data?.list ?? [];
    totalAllUsers.value = rsp.data?.total ?? 0;
  } finally {
    allUsersLoading.value = false;
  }
}
//#endregion

//#region All Users Item
function hasItem(item: UmsUserBase) {
  const found = workerAppUserListAll.value.find(
          value => value.userId === item.id);
  return found ? true : false;
}
//#endregion

//#region WorkerApp User Relation Dialog
const workerAppUserRelationDialogVisible = ref(false);
const workerAppUserRelationDialogTitle = ref('');
const editWorkerAppUserRelation = ref<WmsWorkerAppUserRelationFull>();
const targetWorkerAppUser = ref<UmsUserBase>();

watch([workerAppUserRelationDialogVisible], ([value]) => {
  if (!value) {
    editWorkerAppUserRelation.value = undefined;
  }
});

async function handleEdit(index: number, row: WmsWorkerAppUserRelationFull) {
  console.log(`handleEdit: index ${index}, row ${row.username}`);
  editWorkerAppUserRelation.value = row;
  targetWorkerAppUser.value = undefined;
  workerAppUserRelationDialogTitle.value = `Edit WorkerAppUserRelation for ${editWorkerAppUserRelation.value.username}`;
  workerAppUserRelationDialogVisible.value = true;
}

function onNewWorkerAppUserRelation(row: UmsUserBase) {
  targetWorkerAppUser.value = row;
  workerAppUserRelationDialogTitle.value = `New WorkerAppUserRelation for ${targetWorkerAppUser.value.username}`
  workerAppUserRelationDialogVisible.value = true;
}

function onWorkerAppUserRelationDialogOk() {
  workerAppUserRelationDialogVisible.value = false;
  refreshWorkerAppUserListAll();
}
//#endregion
</script>

<style lang="scss" scoped>
.container {
  width: 100%;
  height: 100%;

  .toolbar {
    margin-bottom: 16px;
    display: flex;
    flex-wrap: wrap;
    align-items: center;

    .keyword-input {
      display: flex;
      align-items: center;
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
