<template>
  <el-card class="container">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="Role Permissions" name="rolePermissions">
        <div>
          <p>Permissions</p>
          <ul>
            <li
              v-for="(item, index) in curRolePermissionList"
              :key="item.name"
            >
              <div class="d-flex">
                <span> {{ item.name }}</span>
                <div class="spacer"></div>
                <el-button
                  text :icon="Delete" @click="deleteItem(index, item)">
                  Delete
                </el-button>
              </div>
            </li>
          </ul>

          <div class="mt-4">
            <el-button
              :disabled="disableBtn"
              type="primary"
              @click="onClickSave"
            >
              Save
            </el-button>

            <el-button
              :disabled="disableBtn"
              class="ml-4"
              @click="emit('cancel')"
            >
              Cancel
            </el-button>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="All Permissions" name="allPermissions">
        <div>
          <div class="d-flex mx-2 mt-2">
            <div class="search-field">
              <el-input v-model="keyword" placeholder="search keyword"
                :disabled="disableBtn">
                <template #append>
                  <el-button :icon="Search"
                    @click="refreshAllPermissionList"
                    />
                </template>
              </el-input>
            </div>
          </div>

          <p class="my-4">Permissions</p>

          <el-table :data="allPermissionList" border class="table" @sort-change="onSortChange">
            <el-table-column prop="name" label="Name" sortable="custom"></el-table-column>
            <el-table-column prop="enabled" label="Enabled" width="80"></el-table-column>
            <el-table-column label="Actions" width="120" align="center">
              <template #default="scope">
                <el-button v-if="!hasItem(scope.row)"
                  text :icon="Plus" @click="addItem(scope.row)">
                  Add
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
              :total="totalAllPermissions"

            ></el-pagination>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, computed } from 'vue';
import type { PropType } from 'vue';

import { Delete, Search, Plus } from '@element-plus/icons-vue';

import { gUserApi } from '@/api';

import type { UmsRole, UmsPermission, UmsPermissionQueryParam } from "@/model/dto/ums";

const props = defineProps({
  role: {
    type: Object as PropType<UmsRole>,
    required: true,
  },
});

const emit = defineEmits(['ok', 'cancel']);

const activeTab = ref('rolePermissions');

//#region tab rolePermissions
const handling = ref(false);

const disableBtn = computed(() => {
  return allPermissionsLoading.value || rolePermissionsLoading.value || handling.value;
})

const rolePermissionsLoading = ref(false);
const origRolePermissionList = ref<UmsPermission[]>([]);
const curRolePermissionList = ref<UmsPermission[]>([]);

onMounted(() => {
  getRolePermissionList();
});

function deleteItem(index: number, item: UmsPermission) {
  console.log('deleteItem', item);
  curRolePermissionList.value.splice(index, 1);
}

async function getRolePermissionList() {
  try {
    rolePermissionsLoading.value = true;

    const result = await gUserApi.getRolePermissionList(props.role.id);

    origRolePermissionList.value = result.data || [];
    curRolePermissionList.value = [...origRolePermissionList.value];
  } finally {
    rolePermissionsLoading.value = false;
  }
}

async function onClickSave() {
  console.log('onClickSave');

  console.log('curRolePermissionList', curRolePermissionList.value);
  console.log('origRolePermissionList', origRolePermissionList.value);

  const addPermissions = [];
  const delPermissions = [];

  for (let permission of curRolePermissionList.value) {
    const found = origRolePermissionList.value.find(
      value => value.id === permission.id
    );

    if (!found) {
      addPermissions.push({
        id: permission.id,
        name: permission.name,
      });
    }
  }

  for (let permission of origRolePermissionList.value) {
    const found = curRolePermissionList.value.find(
      value => value.id === permission.id
    );

    if (!found) {
      delPermissions.push({
        id: permission.id,
        name: permission.name,
      });
    }
  }

  console.log('addPermissions', addPermissions);
  console.log('delPermissions', delPermissions);

  if (!(addPermissions.length || delPermissions.length)) {
    emit('ok');
    return;
  }

  try {
    handling.value = true;

    await gUserApi.updateRolePermissionList({
      roleId: props.role.id,
      data: {
        addPermissions,
        delPermissions,
      } as any,
    });

    emit('ok');
  } finally {
    handling.value = false;
  }
}
//#endregion

//#region tab allPermissions
const keyword = ref('');
const pageNum = ref(1);
const pageSize = ref(10);
let sortBy = '';
let sortDesc: boolean | undefined = undefined;

const allPermissionsLoading = ref(false);
const allPermissionList = ref<UmsPermission[]>([]);
const totalAllPermissions = ref(0);

onMounted(() => {
  refreshAllPermissionList();
});

watch([pageNum, pageSize], () => {
  refreshAllPermissionList();
})

function hasItem(item: UmsPermission) {
  const found = curRolePermissionList.value.find(
          value => value.id === item.id);
  return found ? true : false;
}

function addItem (item: UmsPermission) {
  console.log('addItem', item);
  const found = curRolePermissionList.value.find(
    value => value.id === item.id);
  if (!found) {
    curRolePermissionList.value.push(item);
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

  await refreshAllPermissionList();
}

async function refreshAllPermissionList() {
  try {
    allPermissionsLoading.value = true;

    const queryParam: UmsPermissionQueryParam = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    };

    keyword.value && (queryParam.keyword = keyword.value);

    if (sortBy) {
      queryParam.sortBy = sortBy;
      queryParam.sortDesc = sortDesc;
    }

    const result = await gUserApi.getPermissionList(queryParam);

    allPermissionList.value = result.data?.list ?? [];
    totalAllPermissions.value = result.data?.total ?? 0;
  } finally {
    allPermissionsLoading.value = false;
  }
}
//#endregion
</script>

<style lang="scss" scoped>
.container {
  ul {
    padding-top: 10px;
    padding-left: 0px;
  }

  li {
    list-style-type: none;
  }

  .search-field {
    width: 50%;
    min-width: 200px;
  }

  .pagination {
    margin-top: 10px;
    display: flex;
  }
}
</style>
