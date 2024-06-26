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
              @click="refreshPermissionList"
              />
          </template>
        </el-input>
      </div>

      <div class="spacer"></div>

      <el-button type="primary" :icon="Plus" @click="permissionFormDialogVisible = true">
        New Permission
      </el-button>
    </div>

    <el-table :data="permissionList" border class="table" @sort-change="onSortChange">
      <el-table-column prop="name" label="Name" sortable="custom"></el-table-column>

      <el-table-column label="Actions" width="300" align="center">
        <template #default="scope">
          <el-button
            text :icon="Edit" @click="handleEdit(scope.$index, scope.row)">
            Edit
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
        :total="totalPermissions"
      ></el-pagination>
    </div>

    <el-dialog
      v-if="permissionFormDialogVisible"
      v-model="permissionFormDialogVisible"
      :show-close="false"
    >
      <PermissionForm
        :permission="editPermission"
        @ok="onPermissionFormDialogOk" @cancel="permissionFormDialogVisible = false">
      </PermissionForm>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';

import { Delete, Edit, Search, Plus } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';

import PermissionForm from '@/components/ums/PermissionForm.vue';

import { gUserApi } from '@/api';

import type { UmsPermission, UmsPermissionQueryParam } from '@/model/dto/ums';


const permissionFormDialogVisible = ref(false);

const handling = ref(false);
const loading = ref(false);
const keyword = ref('');
const pageNum = ref(1);
const pageSize = ref(10);
const permissionList = ref([] as UmsPermission[]);
const totalPermissions = ref(0);
const editPermission = ref<UmsPermission>();
let sortBy = '';
let sortDesc: boolean | undefined = undefined;

watch([permissionFormDialogVisible], ([value]) => {
  if (!value) {
    editPermission.value = undefined;
  }
})

watch([pageNum, pageSize], () => {
  refreshPermissionList();
})

refreshPermissionList();

async function refreshPermissionList() {
  console.log(`refreshPermissionList`);

  try {
    loading.value = true;

    const queryParam: UmsPermissionQueryParam = {
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

    const rsp = await gUserApi.getPermissionList(queryParam);

    permissionList.value = rsp.data?.list || [];
    totalPermissions.value = rsp.data?.total || 0;
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

  await refreshPermissionList();
}

async function handleEdit(index: number, row: UmsPermission) {
  console.log(`handleEdit: index ${index}, row ${row.name}`);
  editPermission.value = row;
  permissionFormDialogVisible.value = true;
}

async function handleDelete(index: number, row: UmsPermission) {
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
      await gUserApi.delPermission(row.id);

      await refreshPermissionList();
    }
  }
  finally {
    handling.value = false;
  }
}

function onPermissionFormDialogOk() {
  permissionFormDialogVisible.value = false;
  refreshPermissionList();
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
