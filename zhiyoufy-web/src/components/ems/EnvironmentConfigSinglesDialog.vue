<template>
  <div class="config-single-dialog-container">
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
              @click="refreshConfigSingleList"
              />
          </template>
        </el-input>

        <div class="spacer"></div>

        <el-button
          type="primary" :icon="Plus" @click="onNewConfigSingle">New ConfigSingle</el-button>
      </div>

      <el-table :data="configSingleList" border class="table" @sort-change="onSortChange">
        <el-table-column prop="name" label="Name" sortable="custom"></el-table-column>
        <el-table-column prop="modifiedBy" label="ModifiedBy"  width="160"></el-table-column>
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
              <span>More Actions: </span>

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
          :total="totalConfigSingles"

        ></el-pagination>
      </div>
    </div>

    <el-dialog
      v-if="configSingleFormDialogVisible"
      v-model="configSingleFormDialogVisible"
      :title="configSingleFormTitle"
    >
      <ConfigSingleForm
        :environment="environment"
        :config-single="editConfigSingle"
        @ok="onConfigSingleFormDialogOk"
        @cancel="configSingleFormDialogVisible = false">
      </ConfigSingleForm>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onBeforeMount  } from 'vue';
import type { PropType } from 'vue';

import { Delete, Edit, Search, Plus } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';

import ConfigSingleForm from '@/components/ems/ConfigSingleForm.vue';

import type { EmsEnvironment, EmsConfigSingle, EmsConfigSingleQueryParam } from "@/model/dto/ems";

import { gConfigSingleApi } from '@/api';
import { TimeUtils } from '@/utils';

const props = defineProps({
  environment: {
    type: Object as PropType<EmsEnvironment>,
    required: true,
  },
});

const handling = ref(false);

//#region Config Single List
const loading = ref(false);
const keyword = ref('');
const pageNum = ref(1);
const pageSize = ref(10);
const configSingleList = ref([] as EmsConfigSingle[]);
const totalConfigSingles = ref(0);
let sortBy = '';
let sortDesc: boolean | undefined = undefined;

onBeforeMount(() => {
  refreshConfigSingleList();
});

watch([pageNum, pageSize], () => {
  refreshConfigSingleList();
})

async function refreshConfigSingleList() {
  console.log(`refreshConfigSingleList`);

  try {
    loading.value = true;

    const queryParam: EmsConfigSingleQueryParam = {
      envId: props.environment.id,
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    };

    keyword.value && (queryParam.keyword = keyword.value);

    if (sortBy) {
      queryParam.sortBy = sortBy;
      queryParam.sortDesc = sortDesc;
    }

    const rsp = await gConfigSingleApi.getConfigSingleList(queryParam);

    configSingleList.value = rsp.data?.list || [];
    totalConfigSingles.value = rsp.data?.total || 0;
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

  await refreshConfigSingleList();
}
//#endregion

//#region Config Single List Item
async function handleDelete(index: number, row: EmsConfigSingle) {
  console.log(`handleDelete: index ${index}, row ${row.name}`);

  try {
    handling.value = true;

    const action = await ElMessageBox.confirm(
      `Are you sure to delete config single ${row.name}. Continue?`,
      'Warning',
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
    );

    if (action === 'confirm') {
      await gConfigSingleApi.delConfigSingle(row.id);

      await refreshConfigSingleList();
    }
  }
  finally {
    handling.value = false;
  }
}
//#endregion

//#region Config Single Form
const configSingleFormDialogVisible = ref(false);
const configSingleFormTitle = ref('');
const editConfigSingle = ref<EmsConfigSingle>();

watch([configSingleFormDialogVisible], ([value]) => {
  if (!value) {
    editConfigSingle.value = undefined;
  }
});

async function handleEdit(index: number, row: EmsConfigSingle) {
  console.log(`handleEdit: index ${index}, row ${row.name}`);
  editConfigSingle.value = row;
  configSingleFormTitle.value = `Edit ConfigSingle ${editConfigSingle.value.name}`;
  configSingleFormDialogVisible.value = true;
}

function onNewConfigSingle() {
  configSingleFormTitle.value = 'New ConfigSingle';
  configSingleFormDialogVisible.value = true;
}

function onConfigSingleFormDialogOk() {
  configSingleFormDialogVisible.value = false;
  refreshConfigSingleList();
}
//#endregion
</script>

<style lang="scss" scoped>
.config-single-dialog-container {
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
