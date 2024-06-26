<template>
  <div class="config-item-dialog-container">
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
              @click="refreshConfigItemList"
              />
          </template>
        </el-input>

        <div class="spacer"></div>

        <el-button
          type="primary" :icon="Plus" @click="onNewConfigItem">New ConfigItem</el-button>
      </div>

      <el-table :data="configItemList" border class="table" @sort-change="onSortChange">
        <el-table-column prop="name" label="Name" min-width="300" sortable="custom"></el-table-column>
        <el-table-column prop="sort" label="Sort" sortable="custom"></el-table-column>
        <el-table-column prop="disabled" label="Disabled" sortable="custom"></el-table-column>
        <el-table-column prop="inUse" label="InUse" sortable="custom"></el-table-column>
        <el-table-column prop="usageId" label="UsageId" min-width="300"></el-table-column>
        <el-table-column label="UsageTimeoutAt" width="160" sortable="custom">
          <template #default="scope">
            {{ TimeUtils.displayTimeUptoSecs(scope.row.usageTimeoutAt) }}
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

        <!-- <el-table-column type="expand">
          <template #default="{ $index, row }">
            <div class="p-4">
              <span>More Actions: </span>
            </div>
          </template>
        </el-table-column> -->
      </el-table>

      <div class="pagination">
        <div class="spacer"></div>
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 30]"
          :total="totalConfigItems"

        ></el-pagination>
      </div>
    </div>

    <el-dialog
      v-if="configItemFormDialogVisible"
      v-model="configItemFormDialogVisible"
      :title="configItemFormTitle"
    >
      <ConfigItemForm
        :environment="environment"
        :config-collection="configCollection"
        :config-item="editConfigItem"
        @ok="onConfigItemFormDialogOk"
        @cancel="configItemFormDialogVisible = false">
      </ConfigItemForm>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onBeforeMount  } from 'vue';
import type { PropType } from 'vue';

import { Delete, Edit, Search, Plus } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';

import ConfigItemForm from '@/components/ems/ConfigItemForm.vue';

import type { EmsEnvironment, EmsConfigCollection, EmsConfigItem, EmsConfigItemQueryParam } from "@/model/dto/ems";

import { gConfigItemApi } from '@/api';
import { TimeUtils } from '@/utils';

const props = defineProps({
  environment: {
    type: Object as PropType<EmsEnvironment>,
    required: true,
  },
  configCollection: {
    type: Object as PropType<EmsConfigCollection>,
    required: true,
  },
});

onBeforeMount(() => {
  refreshConfigItemList();
});

const handling = ref(false);

//#region Config Item List
const loading = ref(false);
const keyword = ref('');
const pageNum = ref(1);
const pageSize = ref(10);
const configItemList = ref([] as EmsConfigItem[]);
const totalConfigItems = ref(0);
let sortBy = '';
let sortDesc: boolean | undefined = undefined;

watch([pageNum, pageSize], () => {
  refreshConfigItemList();
})

async function refreshConfigItemList() {
  console.log(`refreshConfigItemList`);

  try {
    loading.value = true;

    const queryParam: EmsConfigItemQueryParam = {
      collectionId: props.configCollection.id,
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    };

    keyword.value && (queryParam.keyword = keyword.value);

    if (sortBy) {
      queryParam.sortBy = sortBy;
      queryParam.sortDesc = sortDesc;
    }

    const rsp = await gConfigItemApi.getConfigItemList(queryParam);

    configItemList.value = rsp.data?.list || [];
    totalConfigItems.value = rsp.data?.total || 0;
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
    if (prop == 'inUse') {
      sortBy = 'in_use';
    } else if (prop) {
      sortBy = prop;
    } else if (column.label === 'UsageTimeoutAt') {
      sortBy = 'usage_timeout_at';
    } else {
      throw new Error(`unexpected column ${column.label}`);
    }
  }

  console.log(`sortBy ${sortBy}, sortDesc ${sortDesc}`);

  await refreshConfigItemList();
}
//#endregion

//#region Config Item List Item
async function handleDelete(index: number, row: EmsConfigItem) {
  console.log(`handleDelete: index ${index}, row ${row.name}`);

  try {
    handling.value = true;

    const action = await ElMessageBox.confirm(
      `Are you sure to delete config item ${row.name}. Continue?`,
      'Warning',
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
    );

    if (action === 'confirm') {
      await gConfigItemApi.delConfigItem(row.id);

      await refreshConfigItemList();
    }
  }
  finally {
    handling.value = false;
  }
}
//#endregion

//#region Config Item Form
const configItemFormDialogVisible = ref(false);
const configItemFormTitle = ref('');
const editConfigItem = ref<EmsConfigItem>();

watch([configItemFormDialogVisible], ([value]) => {
  if (!value) {
    editConfigItem.value = undefined;
  }
});

async function handleEdit(index: number, row: EmsConfigItem) {
  console.log(`handleEdit: index ${index}, row ${row.name}`);
  editConfigItem.value = row;
  configItemFormTitle.value = `Edit ConfigItem ${editConfigItem.value.name}`;
  configItemFormDialogVisible.value = true;
}

function onNewConfigItem() {
  configItemFormTitle.value = 'New ConfigItem';
  configItemFormDialogVisible.value = true;
}

function onConfigItemFormDialogOk() {
  configItemFormDialogVisible.value = false;
  refreshConfigItemList();
}
//#endregion
</script>

<style lang="scss" scoped>
.config-item-dialog-container {
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
