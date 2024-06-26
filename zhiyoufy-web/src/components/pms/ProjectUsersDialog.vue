<template>
  <el-card class="container">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="Project Users" name="Project Users">
        <div>
          <el-table :data="projectUserListPage" border>
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
              v-model:current-page="projectUsersPageNum"
              v-model:page-size="projectUsersPageSize"
              :page-sizes="[10, 20, 30]"
              :total="totalProjectUsers"

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
                  text :icon="Plus" @click="onNewProjectUserRelation(scope.row)">
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
      v-if="projectUserRelationDialogVisible"
      v-model="projectUserRelationDialogVisible"
      :title="projectUserRelationDialogTitle"
    >
      <ProjectUserRelationDialog
        :project="project"
        :project-user-relation="editProjectUserRelation"
        :user-base="targetProjectUser"
        @ok="onProjectUserRelationDialogOk"
        @cancel="projectUserRelationDialogVisible = false">
      </ProjectUserRelationDialog>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { ref, watch, onBeforeMount, computed } from 'vue';
import type { PropType } from 'vue';

import { Delete, Edit, Search, Plus } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';

import ProjectUserRelationDialog from '@/components/pms/ProjectUserRelationDialog.vue';

import { gUserApi, gProjectApi } from '@/api';

import type { PageQueryParam } from "@/model/dto/common";
import type { UmsUserBase, UmsUserQueryParam } from '@/model/dto/ums';
import type { PmsProject, PmsProjectUserRelationFull } from "@/model/dto/pms";

const props = defineProps({
  project: {
    type: Object as PropType<PmsProject>,
    required: true,
  },
});

const activeTab = ref('Project Users');

const handling = ref(false);

//#region Project Users List
onBeforeMount(() => {
  refreshProjectUserListAll();
})

const projectUsersLoading = ref(false);
const projectUsersPageNum = ref(1);
const projectUsersPageSize = ref(10);
const projectUserListAll = ref<PmsProjectUserRelationFull[]>([]);
const totalProjectUsers = computed(() => {
  return projectUserListAll.value.length;
});
const projectUserListPage = computed(() => {
  const start = (projectUsersPageNum.value - 1) * projectUsersPageSize.value;
  const end = projectUsersPageNum.value * projectUsersPageSize.value;
  let targetProjectUserList = projectUserListAll.value.slice(start, end);
  return targetProjectUserList;
})

async function refreshProjectUserListAll() {
  try {
    projectUsersLoading.value = true;

    const params: PageQueryParam = {
      pageNum: 1,
      pageSize: 10000,
    };

    const rsp = await gProjectApi.getProjectUserList({projectId: props.project.id, params});

    projectUserListAll.value = rsp.data?.list || [];
  } finally {
    projectUsersLoading.value = false;
  }
}
//#endregion

//#region Project Users Item
async function handleDelete(index: number, row: PmsProjectUserRelationFull) {
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
      await gProjectApi.delProjectUser(row.id);

      await refreshProjectUserListAll();
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
  const found = projectUserListAll.value.find(
          value => value.userId === item.id);
  return found ? true : false;
}
//#endregion

//#region Project User Relation Dialog
const projectUserRelationDialogVisible = ref(false);
const projectUserRelationDialogTitle = ref('');
const editProjectUserRelation = ref<PmsProjectUserRelationFull>();
const targetProjectUser = ref<UmsUserBase>();

watch([projectUserRelationDialogVisible], ([value]) => {
  if (!value) {
    editProjectUserRelation.value = undefined;
  }
});

async function handleEdit(index: number, row: PmsProjectUserRelationFull) {
  console.log(`handleEdit: index ${index}, row ${row.username}`);
  editProjectUserRelation.value = row;
  targetProjectUser.value = undefined;
  projectUserRelationDialogTitle.value = `Edit ProjectUserRelation for ${editProjectUserRelation.value.username}`;
  projectUserRelationDialogVisible.value = true;
}

function onNewProjectUserRelation(row: UmsUserBase) {
  targetProjectUser.value = row;
  projectUserRelationDialogTitle.value = `New ProjectUserRelation for ${targetProjectUser.value.username}`
  projectUserRelationDialogVisible.value = true;
}

function onProjectUserRelationDialogOk() {
  projectUserRelationDialogVisible.value = false;
  refreshProjectUserListAll();
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
