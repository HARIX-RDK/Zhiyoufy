<template>
  <el-card class="container">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="User Roles" name="userRoles">
        <div>
          <p>Roles</p>
          <ul>
            <li
              v-for="(item, index) in curUserRoleList"
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
      <el-tab-pane label="All Roles" name="allRoles">
        <div>
          <div class="d-flex mx-2 mt-2">
            <div class="search-field">
              <el-input v-model="keyword" placeholder="search keyword"
                :disabled="disableBtn">
                <template #append>
                  <el-button :icon="Search"
                    @click="refreshAllRoleList"
                    />
                </template>
              </el-input>
            </div>
          </div>

          <p class="my-4">Roles</p>

          <el-table :data="allRoleList" border class="table" @sort-change="onSortChange">
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
              :total="totalAllRoles"

            ></el-pagination>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import type { PropType } from 'vue';

import { Delete, Search, Plus } from '@element-plus/icons-vue';

import { gUserApi } from '@/api';

import type { UmsUserDTO, UmsRole, UmsRoleQueryParam } from "@/model/dto/ums";

const props = defineProps({
  user: {
    type: Object as PropType<UmsUserDTO>,
    required: true,
  },
});

const emit = defineEmits(['ok', 'cancel']);

const activeTab = ref('userRoles');

//#region tab userRoles
const handling = ref(false);

const userRolesLoading = ref(false);
const origUserRoleList = ref<UmsRole[]>([]);
const curUserRoleList = ref<UmsRole[]>([]);

const disableBtn = computed(() => {
  return userRolesLoading.value || handling.value;
})

getUserRoleList();

function deleteItem(index: number, item: UmsRole) {
  console.log('deleteItem', item);
  curUserRoleList.value.splice(index, 1);
}

async function getUserRoleList() {
  try {
    userRolesLoading.value = true;

    const userRoleListResult = await gUserApi.getUserRoleList(props.user.id);

    origUserRoleList.value = userRoleListResult.data || [];
    curUserRoleList.value = [...origUserRoleList.value];
  } finally {
    userRolesLoading.value = false;
  }
}

async function onClickSave() {
  console.log('onClickSave');

  console.log('curUserRoleList', curUserRoleList.value);
  console.log('origUserRoleList', origUserRoleList.value);

  const addRoles = [];
  const delRoles = [];

  for (let role of curUserRoleList.value) {
    const found = origUserRoleList.value.find(
      value => value.id === role.id
    );

    if (!found) {
      addRoles.push({
        id: role.id,
        name: role.name,
      });
    }
  }

  for (let role of origUserRoleList.value) {
    const found = curUserRoleList.value.find(
      value => value.id === role.id
    );

    if (!found) {
      delRoles.push({
        id: role.id,
        name: role.name,
      });
    }
  }

  console.log('addRoles', addRoles);
  console.log('delRoles', delRoles);

  if (!(addRoles.length || delRoles.length)) {
    emit('ok');
    return;
  }

  try {
    handling.value = true;

    await gUserApi.updateUserRoleList({
      userId: props.user.id,
      data: {
        addRoles,
        delRoles,
      } as any,
    });

    emit('ok');
  } finally {
    handling.value = false;
  }
}
//#endregion

//#region tab allRoles
const keyword = ref('');
const pageNum = ref(1);
const pageSize = ref(10);
let sortBy = '';
let sortDesc: boolean | undefined = undefined;

const allRolesLoading = ref(false);
const allRoleList = ref<UmsRole[]>([]);
const totalAllRoles = ref(0);

refreshAllRoleList();

watch([pageNum, pageSize], () => {
  refreshAllRoleList();
})

function hasItem(item: UmsRole) {
  const found = curUserRoleList.value.find(
          value => value.id === item.id);
  return found ? true : false;
}

function addItem (item: UmsRole) {
  console.log('addItem', item);
  const found = curUserRoleList.value.find(
    value => value.id === item.id);
  if (!found) {
    curUserRoleList.value.push(item);
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

  await refreshAllRoleList();
}

async function refreshAllRoleList() {
  try {
    allRolesLoading.value = true;

    const queryParam: UmsRoleQueryParam = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    };

    keyword.value && (queryParam.keyword = keyword.value);

    if (sortBy) {
      queryParam.sortBy = sortBy;
      queryParam.sortDesc = sortDesc;
    }

    const roleListResult = await gUserApi.getRoleList(queryParam);

    allRoleList.value = roleListResult.data?.list ?? [];
    totalAllRoles.value = roleListResult.data?.total ?? 0;
  } finally {
    allRolesLoading.value = false;
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
