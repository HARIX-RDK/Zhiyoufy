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
            @click="refreshProjectList"
            />
        </template>
      </el-input>

      <el-checkbox v-if="userStore.isAdmin"
        class="ml-2"
        v-model="allUsers" label="All Users" />

      <div class="spacer"></div>

      <el-button
        v-if="hasProjectOwnerRole()"
        type="primary" :icon="Plus" @click="onNewProject">New Project</el-button>
    </div>

    <el-table :data="projectList" border class="table" @sort-change="onSortChange">
      <el-table-column prop="name" label="Name" sortable="custom"></el-table-column>
      <el-table-column prop="description" label="Description" width="400"
        show-overflow-tooltip></el-table-column>

      <el-table-column label="Actions" width="160" align="center">
        <template #default="scope">
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
        :total="totalProjects"

      ></el-pagination>
    </div>

    <el-dialog
      v-if="projectFormDialogVisible"
      v-model="projectFormDialogVisible"
      :title="projectFormDialogTitle"
    >
      <ProjectForm
        :project="editProject"
        @ok="onProjectFormDialogOk" @cancel="projectFormDialogVisible = false">
      </ProjectForm>
    </el-dialog>

    <el-dialog
      v-if="projectUserDialogVisible"
      v-model="projectUserDialogVisible"
      :title="projectUserDialogTitle"
      fullscreen>
      <ProjectUsersDialog
        :project="editProject!"
      >
      </ProjectUsersDialog>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onBeforeMount } from 'vue';

import { Delete, Edit, Search, Plus } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';

import ProjectForm from '@/components/pms/ProjectForm.vue';
import ProjectUsersDialog from '@/components/pms/ProjectUsersDialog.vue';

import { useUserStore } from '@/stores/user';
import { gProjectApi } from '@/api';
import { PermissionUtils } from '@/utils';

import type { PmsProjectFull, PmsProjectQueryParam } from '@/model/dto/pms';


const userStore = useUserStore();

const handling = ref(false);

//#region Project List
const allUsers = ref(false);
const loading = ref(false);
const keyword = ref('');
const pageNum = ref(1);
const pageSize = ref(10);
const projectList = ref([] as PmsProjectFull[]);
const totalProjects = ref(0);

let sortBy = '';
let sortDesc: boolean | undefined = undefined;

onBeforeMount(() => {
  refreshProjectList();
})

watch([pageNum, pageSize], () => {
  refreshProjectList();
})

async function refreshProjectList() {
  console.log(`refreshProjectList`);

  try {
    loading.value = true;

    const queryParam: PmsProjectQueryParam = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    };

    keyword.value && (queryParam.keyword = keyword.value);
    allUsers.value && (queryParam.allUsers = allUsers.value);

    if (sortBy) {
      queryParam.sortBy = sortBy;
      queryParam.sortDesc = sortDesc;
    }

    const rsp = await gProjectApi.getProjectList(queryParam);

    projectList.value = rsp.data?.list || [];
    totalProjects.value = rsp.data?.total || 0;
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

  await refreshProjectList();
}
//#endregion

//#region Project List Item
async function handleDelete(index: number, row: PmsProjectFull) {
  console.log(`handleDelete: index ${index}, row ${row.name}`);

  try {
    handling.value = true;

    const action = await ElMessageBox.confirm(
      `Are you sure to delete project ${row.name}. Continue?`,
      'Warning',
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
    );

    if (action === 'confirm') {
      await gProjectApi.delProject(row.id);

      await refreshProjectList();
    }
  }
  finally {
    handling.value = false;
  }
}

function hasProjectOwnerRole() {
  return PermissionUtils.hasRole('project.owner');
}

function isEditor(item: PmsProjectFull) {
  return allUsers.value || item.isOwner || item.isEditor;
}

function isOwner(item: PmsProjectFull) {
  return allUsers.value || item.isOwner;
}
//#endregion

//#region Project Form
const projectFormDialogVisible = ref(false);
const projectFormDialogTitle = ref('');
const editProject = ref<PmsProjectFull>();

watch([projectFormDialogVisible], ([value]) => {
  if (!value) {
    editProject.value = undefined;
  }
});

async function handleEdit(index: number, row: PmsProjectFull) {
  console.log(`handleEdit: index ${index}, row ${row.name}`);
  editProject.value = row;
  projectFormDialogTitle.value = 'Edit Project';
  projectFormDialogVisible.value = true;
}

function onNewProject() {
  projectFormDialogTitle.value = 'New Project';
  projectFormDialogVisible.value = true;
}

function onProjectFormDialogOk() {
  projectFormDialogVisible.value = false;
  refreshProjectList();
}
//#endregion

//#region Project User
const projectUserDialogVisible = ref(false);
const projectUserDialogTitle = ref('');

async function handleEditUsers(index: number, row: PmsProjectFull) {
  console.log(`handleEditUsers: index ${index}, row ${row.name}`);
  editProject.value = row;
  projectUserDialogTitle.value = `Users of Project:  ${editProject.value.name}`;
  projectUserDialogVisible.value = true;
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
