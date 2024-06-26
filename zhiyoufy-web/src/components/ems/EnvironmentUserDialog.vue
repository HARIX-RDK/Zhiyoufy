<template>
  <el-card class="container">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="Environment Users" name="Environment Users">
        <div>
          <el-table :data="environmentUserListPage" border>
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
              v-model:current-page="environmentUsersPageNum"
              v-model:page-size="environmentUsersPageSize"
              :page-sizes="[10, 20, 30]"
              :total="totalEnvironmentUsers"

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
                  text :icon="Plus" @click="onNewEnvironmentUserRelation(scope.row)">
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
      v-if="environmentUserRelationDialogVisible"
      v-model="environmentUserRelationDialogVisible"
      :title="environmentUserRelationDialogTitle"
    >
      <EnvironmentUserRelationDialog
        :environment="environment"
        :environment-user-relation="editEnvironmentUserRelation"
        :user-base="targetEnvironmentUser"
        @ok="onEnvironmentUserRelationDialogOk"
        @cancel="environmentUserRelationDialogVisible = false">
      </EnvironmentUserRelationDialog>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { ref, watch, onBeforeMount, computed } from 'vue';
import type { PropType } from 'vue';

import { Delete, Edit, Search, Plus } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';

import EnvironmentUserRelationDialog from '@/components/ems/EnvironmentUserRelationDialog.vue';

import { gUserApi, gEnvironmentApi } from '@/api';

import type { PageQueryParam } from "@/model/dto/common";
import type { UmsUserBase, UmsUserQueryParam } from '@/model/dto/ums';
import type { EmsEnvironment, EmsEnvironmentUserRelationFull } from "@/model/dto/ems";

const props = defineProps({
  environment: {
    type: Object as PropType<EmsEnvironment>,
    required: true,
  },
});

const activeTab = ref('Environment Users');

const handling = ref(false);

//#region Environment Users List
onBeforeMount(() => {
  refreshEnvironmentUserListAll();
})

const environmentUsersLoading = ref(false);
const environmentUsersPageNum = ref(1);
const environmentUsersPageSize = ref(10);
const environmentUserListAll = ref<EmsEnvironmentUserRelationFull[]>([]);
const totalEnvironmentUsers = computed(() => {
  return environmentUserListAll.value.length;
});
const environmentUserListPage = computed(() => {
  const start = (environmentUsersPageNum.value - 1) * environmentUsersPageSize.value;
  const end = environmentUsersPageNum.value * environmentUsersPageSize.value;
  let targetEnvironmentUserList = environmentUserListAll.value.slice(start, end);
  return targetEnvironmentUserList;
})

async function refreshEnvironmentUserListAll() {
  try {
    environmentUsersLoading.value = true;

    const params: PageQueryParam = {
      pageNum: 1,
      pageSize: 10000,
    };

    const rsp = await gEnvironmentApi.getEnvironmentUserList({envId: props.environment.id, params});

    environmentUserListAll.value = rsp.data?.list || [];
  } finally {
    environmentUsersLoading.value = false;
  }
}
//#endregion

//#region Environment Users Item
async function handleDelete(index: number, row: EmsEnvironmentUserRelationFull) {
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
      await gEnvironmentApi.delEnvironmentUser(row.id);

      await refreshEnvironmentUserListAll();
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
  const found = environmentUserListAll.value.find(
          value => value.userId === item.id);
  return found ? true : false;
}
//#endregion

//#region Environment User Relation Dialog
const environmentUserRelationDialogVisible = ref(false);
const environmentUserRelationDialogTitle = ref('');
const editEnvironmentUserRelation = ref<EmsEnvironmentUserRelationFull>();
const targetEnvironmentUser = ref<UmsUserBase>();

watch([environmentUserRelationDialogVisible], ([value]) => {
  if (!value) {
    editEnvironmentUserRelation.value = undefined;
  }
});

async function handleEdit(index: number, row: EmsEnvironmentUserRelationFull) {
  console.log(`handleEdit: index ${index}, row ${row.username}`);
  editEnvironmentUserRelation.value = row;
  targetEnvironmentUser.value = undefined;
  environmentUserRelationDialogTitle.value = `Edit EnvironmentUserRelation for ${editEnvironmentUserRelation.value.username}`;
  environmentUserRelationDialogVisible.value = true;
}

function onNewEnvironmentUserRelation(row: UmsUserBase) {
  targetEnvironmentUser.value = row;
  environmentUserRelationDialogTitle.value = `New EnvironmentUserRelation for ${targetEnvironmentUser.value.username}`
  environmentUserRelationDialogVisible.value = true;
}

function onEnvironmentUserRelationDialogOk() {
  environmentUserRelationDialogVisible.value = false;
  refreshEnvironmentUserListAll();
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
