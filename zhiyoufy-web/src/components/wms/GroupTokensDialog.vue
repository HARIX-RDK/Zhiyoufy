<template>
  <div class="dialog-container">
    <div class="dialog-content">
      <div class="toolbar">
        <span>Search</span>
        <el-input
          class="ml-1 keyword-input"
          v-model="keyword"
          placeholder="keyword"
        >
          <template #append>
            <el-button :icon="Search"
              @click="refreshGroupTokenList"
              />
          </template>
        </el-input>

        <div class="spacer"></div>

        <el-button type="primary" :icon="Plus" @click="onNewGroupToken">
          New GroupToken
        </el-button>
      </div>

      <el-table :data="groupTokenList" border class="table" @sort-change="onSortChange">
        <el-table-column prop="name" label="Name" sortable="custom"></el-table-column>
        <el-table-column prop="modifiedBy" label="ModifiedBy"  width="200"></el-table-column>
        <el-table-column label="ModifiedTime" width="160" sortable="custom">
          <template #default="scope">
            {{ TimeUtils.displayTimeUptoSecs(scope.row.modifiedTime) }}
          </template>
        </el-table-column>

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

        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="p-4">
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
          :total="totalGroupTokens"

        ></el-pagination>
      </div>
    </div>

    <el-dialog
      v-if="groupTokenFormDialogVisible"
      v-model="groupTokenFormDialogVisible"
      :title="groupTokenFormTitle"
    >
      <GroupTokenForm
        :worker-app="workerApp"
        :worker-group="workerGroup"
        :group-token="editGroupToken"
        @ok="onGroupTokenFormDialogOk"
        @cancel="groupTokenFormDialogVisible = false">
      </GroupTokenForm>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onBeforeMount  } from 'vue';
import type { PropType } from 'vue';

import { Delete, Edit, Search, Plus } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';

import GroupTokenForm from '@/components/wms/GroupTokenForm.vue';

import type { WmsWorkerApp, WmsWorkerGroup, WmsGroupToken, WmsGroupTokenQueryParam } from "@/model/dto/wms";

import { gGroupTokenApi } from '@/api';
import { TimeUtils } from '@/utils';

const props = defineProps({
  workerApp: {
    type: Object as PropType<WmsWorkerApp>,
    required: true,
  },
  workerGroup: {
    type: Object as PropType<WmsWorkerGroup>,
    required: true,
  },
});

const handling = ref(false);

//#region Group Token List
const loading = ref(false);
const keyword = ref('');
const pageNum = ref(1);
const pageSize = ref(10);
const groupTokenList = ref([] as WmsGroupToken[]);
const totalGroupTokens = ref(0);
let sortBy = '';
let sortDesc: boolean | undefined = undefined;

onBeforeMount(() => {
  refreshGroupTokenList();
});

watch([pageNum, pageSize], () => {
  refreshGroupTokenList();
})

async function refreshGroupTokenList() {
  console.log(`refreshGroupTokenList`);

  try {
    loading.value = true;

    const queryParam: WmsGroupTokenQueryParam = {
      workerGroupId: props.workerGroup.id,
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    };

    keyword.value && (queryParam.keyword = keyword.value);

    if (sortBy) {
      queryParam.sortBy = sortBy;
      queryParam.sortDesc = sortDesc;
    }

    const rsp = await gGroupTokenApi.getGroupTokenList(queryParam);

    groupTokenList.value = rsp.data?.list || [];
    totalGroupTokens.value = rsp.data?.total || 0;
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
    } else if (column.label === 'ModifiedTime') {
      sortBy = 'modified_time';
    } else {
      throw new Error(`unexpected column ${column.label}`);
    }
  }

  console.log(`sortBy ${sortBy}, sortDesc ${sortDesc}`);

  await refreshGroupTokenList();
}
//#endregion

//#region Group Token List Item
async function handleDelete(index: number, row: WmsGroupToken) {
  console.log(`handleDelete: index ${index}, row ${row.name}`);

  try {
    handling.value = true;

    const action = await ElMessageBox.confirm(
      `Are you sure to delete group token ${row.name}. Continue?`,
      'Warning',
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
    );

    if (action === 'confirm') {
      await gGroupTokenApi.delGroupToken(row.id);

      await refreshGroupTokenList();
    }
  }
  finally {
    handling.value = false;
  }
}
//#endregion

//#region Group Token Form
const groupTokenFormDialogVisible = ref(false);
const groupTokenFormTitle = ref('');
const editGroupToken = ref<WmsGroupToken>();

watch([groupTokenFormDialogVisible], ([value]) => {
  if (!value) {
    editGroupToken.value = undefined;
  }
});

async function handleEdit(index: number, row: WmsGroupToken) {
  console.log(`handleEdit: index ${index}, row ${row.name}`);
  editGroupToken.value = row;
  groupTokenFormTitle.value = `Edit GroupToken ${editGroupToken.value.name}`;
  groupTokenFormDialogVisible.value = true;
}

function onNewGroupToken() {
  groupTokenFormTitle.value = 'New GroupToken';
  groupTokenFormDialogVisible.value = true;
}

function onGroupTokenFormDialogOk() {
  groupTokenFormDialogVisible.value = false;
  refreshGroupTokenList();
}
//#endregion
</script>

<style lang="scss" scoped>
.dialog-container {
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
