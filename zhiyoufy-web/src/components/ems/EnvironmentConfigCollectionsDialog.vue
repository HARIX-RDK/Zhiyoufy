<template>
  <div class="config-collection-dialog-container">
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
              @click="refreshConfigCollectionList"
              />
          </template>
        </el-input>

        <div class="spacer"></div>

        <el-button
          type="primary" :icon="Plus" @click="onNewConfigCollection">New ConfigCollection</el-button>
      </div>

      <el-table :data="configCollectionList" border class="table" @sort-change="onSortChange">
        <el-table-column prop="name" label="Name" sortable="custom"></el-table-column>
        <el-table-column prop="modifiedBy" label="ModifiedBy"></el-table-column>
        <el-table-column label="ModifiedTime" width="160" sortable="custom">
          <template #default="scope">
            {{ TimeUtils.displayTimeUptoSecs(scope.row.modifiedTime) }}
          </template>
        </el-table-column>

        <el-table-column label="Actions" width="400" align="center">
          <template #default="scope">
            <el-button
              text :icon="Edit" @click="handleEditConfigItems(scope.$index, scope.row)">
              ConfigItems
            </el-button>

            <el-button
              text :icon="Edit" @click="handleEdit(scope.$index, scope.row)">
              Edit
            </el-button>
          </template>
        </el-table-column>

        <el-table-column type="expand">
          <template #default="{ $index, row }">
            <div class="p-4">
              <span>More Actions: </span>
              <el-button
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
          :total="totalConfigCollections"

        ></el-pagination>
      </div>
    </div>

    <el-dialog
      v-if="configCollectionFormDialogVisible"
      v-model="configCollectionFormDialogVisible"
      :title="configCollectionFormTitle"
    >
      <ConfigCollectionForm
        :environment="environment"
        :config-collection="editConfigCollection"
        @ok="onConfigCollectionFormDialogOk"
        @cancel="configCollectionFormDialogVisible = false">
      </ConfigCollectionForm>
    </el-dialog>

    <el-dialog
      v-if="configItemsDialogVisible"
      v-model="configItemsDialogVisible"
      :title="configItemsDialogTitle"
      fullscreen>
      <EnvironmentConfigItemsDialog
        :environment="environment"
        :config-collection="editConfigCollection!"
      >
      </EnvironmentConfigItemsDialog>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onBeforeMount  } from 'vue';
import type { PropType } from 'vue';

import { Delete, Edit, Search, Plus } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';

import ConfigCollectionForm from '@/components/ems/ConfigCollectionForm.vue';
import EnvironmentConfigItemsDialog from '@/components/ems/EnvironmentConfigItemsDialog.vue';

import type { EmsEnvironment, EmsConfigCollection, EmsConfigCollectionQueryParam } from "@/model/dto/ems";

import { gConfigCollectionApi } from '@/api';
import { TimeUtils } from '@/utils';

const props = defineProps({
  environment: {
    type: Object as PropType<EmsEnvironment>,
    required: true,
  },
});

onBeforeMount(() => {
  refreshConfigCollectionList();
});

const handling = ref(false);

//#region Config Collection List
const loading = ref(false);
const keyword = ref('');
const pageNum = ref(1);
const pageSize = ref(10);
const configCollectionList = ref([] as EmsConfigCollection[]);
const totalConfigCollections = ref(0);
let sortBy = '';
let sortDesc: boolean | undefined = undefined;

watch([pageNum, pageSize], () => {
  refreshConfigCollectionList();
})

async function refreshConfigCollectionList() {
  console.log(`refreshConfigCollectionList`);

  try {
    loading.value = true;

    const queryParam: EmsConfigCollectionQueryParam = {
      envId: props.environment.id,
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    };

    keyword.value && (queryParam.keyword = keyword.value);

    if (sortBy) {
      queryParam.sortBy = sortBy;
      queryParam.sortDesc = sortDesc;
    }

    const rsp = await gConfigCollectionApi.getConfigCollectionList(queryParam);

    configCollectionList.value = rsp.data?.list || [];
    totalConfigCollections.value = rsp.data?.total || 0;
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

  await refreshConfigCollectionList();
}
//#endregion

//#region Config Collection List Item
async function handleDelete(index: number, row: EmsConfigCollection) {
  console.log(`handleDelete: index ${index}, row ${row.name}`);

  try {
    handling.value = true;

    const action = await ElMessageBox.confirm(
      `Are you sure to delete config collection ${row.name}. Continue?`,
      'Warning',
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
    );

    if (action === 'confirm') {
      await gConfigCollectionApi.delConfigCollection(row.id);

      await refreshConfigCollectionList();
    }
  }
  finally {
    handling.value = false;
  }
}
//#endregion

//#region Config Collection Form
const configCollectionFormDialogVisible = ref(false);
const configCollectionFormTitle = ref('');
const editConfigCollection = ref<EmsConfigCollection>();

watch([configCollectionFormDialogVisible], ([value]) => {
  if (!value) {
    editConfigCollection.value = undefined;
  }
});

async function handleEdit(index: number, row: EmsConfigCollection) {
  console.log(`handleEdit: index ${index}, row ${row.name}`);
  editConfigCollection.value = row;
  configCollectionFormTitle.value = `Edit ConfigCollection ${editConfigCollection.value.name}`;
  configCollectionFormDialogVisible.value = true;
}

function onNewConfigCollection() {
  configCollectionFormTitle.value = 'New ConfigCollection';
  configCollectionFormDialogVisible.value = true;
}

function onConfigCollectionFormDialogOk() {
  configCollectionFormDialogVisible.value = false;
  refreshConfigCollectionList();
}
//#endregion

//#region Config Items Dialog
const configItemsDialogVisible = ref(false);
const configItemsDialogTitle = ref('');

async function handleEditConfigItems(index: number, row: EmsConfigCollection) {
  console.log(`handleEditConfigItems: index ${index}, row ${row.name}`);
  editConfigCollection.value = row;
  configItemsDialogTitle.value = `Collection Config Items For ${editConfigCollection.value.name}`;
  configItemsDialogVisible.value = true;
}
//#endregion
</script>

<style lang="scss" scoped>
.config-collection-dialog-container {
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
