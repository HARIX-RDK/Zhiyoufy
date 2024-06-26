<template>
  <div class="container">
    <div class="toolbar">
      <div class="keyword-input">
        <span>Search</span>
        <el-input
          class="ml-1"
          v-model="keyword"
          placeholder="keyword"
        >
          <template #append>
            <el-button :icon="Search"
              @click="refreshRoleList"
              />
          </template>
        </el-input>
      </div>

      <div class="spacer"></div>

      <el-button type="primary" :icon="Plus" @click="roleFormDialogVisible = true">
        New Role
      </el-button>
    </div>

    <el-table :data="roleList" border class="table" @sort-change="onSortChange">
      <el-table-column prop="name" label="Name" sortable="custom"></el-table-column>
      <el-table-column prop="enabled" label="Enabled" width="80"></el-table-column>

      <el-table-column label="Actions" width="400" align="center">
        <template #default="scope">
          <el-button
            text :icon="Edit" @click="handleEdit(scope.$index, scope.row)">
            Edit
          </el-button>
          <el-button
            text :icon="Edit" @click="handleEditPermissions(scope.$index, scope.row)">
            Permissions
          </el-button>
          <el-button
            text :icon="Delete" class="red" @click="handleDelete(scope.$index, scope.row)">
            Delete
          </el-button>
        </template>
      </el-table-column>

      <el-table-column type="expand">
        <template #default="scope">
          <div class="p-4">
            <p>Description: {{ scope.row.description }}</p>
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
        :total="totalRoles"
      ></el-pagination>
    </div>

    <el-dialog
      v-if="roleFormDialogVisible"
      v-model="roleFormDialogVisible"
      :show-close="false"
    >
      <RoleForm
        :role="editRole"
        @ok="onRoleFormDialogOk" @cancel="roleFormDialogVisible = false">
      </RoleForm>
    </el-dialog>

    <el-dialog
      v-if="rolePermissionDialogVisible"
      top="5vh"
      v-model="rolePermissionDialogVisible"
    >
      <RolePermissionDialog
        :role="editRole!"
        @ok="rolePermissionDialogVisible = false"
        @cancel="rolePermissionDialogVisible = false">
      </RolePermissionDialog>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';

import { Delete, Edit, Search, Plus } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';

import RoleForm from '@/components/ums/RoleForm.vue';
import RolePermissionDialog from '@/components/ums/RolePermissionDialog.vue';

import { gUserApi } from '@/api';

import type { UmsRole, UmsRoleQueryParam } from '@/model/dto/ums';


const roleFormDialogVisible = ref(false);
const rolePermissionDialogVisible = ref(false);

const handling = ref(false);
const loading = ref(false);
const keyword = ref('');
const pageNum = ref(1);
const pageSize = ref(10);
const roleList = ref([] as UmsRole[]);
const totalRoles = ref(0);
const editRole = ref<UmsRole>();
let sortBy = '';
let sortDesc: boolean | undefined = undefined;

watch([roleFormDialogVisible], ([value]) => {
  if (!value) {
    editRole.value = undefined;
  }
})

watch([pageNum, pageSize], () => {
  refreshRoleList();
})

refreshRoleList();

async function refreshRoleList() {
  console.log(`refreshRoleList`);

  try {
    loading.value = true;

    const queryParam: UmsRoleQueryParam = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    };

    if (keyword.value) {
      queryParam.keyword = keyword.value;
    }

    if (sortBy) {
      queryParam.sortBy = sortBy;
      queryParam.sortDesc = sortDesc;
    }

    const rsp = await gUserApi.getRoleList(queryParam);

    roleList.value = rsp.data?.list || [];
    totalRoles.value = rsp.data?.total || 0;
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

  await refreshRoleList();
}

async function handleEdit(index: number, row: UmsRole) {
  console.log(`handleEdit: index ${index}, row ${row.name}`);
  editRole.value = row;
  roleFormDialogVisible.value = true;
}

async function handleEditPermissions(index: number, row: UmsRole) {
  console.log(`handleEditPermissions: index ${index}, row ${row.name}`);
  editRole.value = row;
  rolePermissionDialogVisible.value = true;
}

async function handleDelete(index: number, row: UmsRole) {
  console.log(`handleDelete: index ${index}, row ${row.name}`);

  try {
    handling.value = true;

    const action = await ElMessageBox.confirm(
      `Are you sure to delete role ${row.name}. Continue?`,
      'Warning',
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
    );

    if (action === 'confirm') {
      await gUserApi.delRole(row.id);

      await refreshRoleList();
    }
  }
  finally {
    handling.value = false;
  }
}

function onRoleFormDialogOk() {
  roleFormDialogVisible.value = false;
  refreshRoleList();
}
</script>

<style lang="scss" scoped>
.container {
  padding: 10px;
  height: 100%;

  .toolbar {
    margin-bottom: 16px;
    display: flex;
    flex-wrap: wrap;

    .keyword-input {
      display: flex;
      align-items: center;
      width: 250px;
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
