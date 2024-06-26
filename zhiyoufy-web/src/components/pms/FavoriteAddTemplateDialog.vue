<template>
  <div class="container">
    <div class="table-container">
      <div class="toolbar">
        <el-input v-model="keywordTemplate" placeholder="search keyword" class="table-search-field">
          <template #append>
            <el-button :icon="Search" @click="refreshJobTemplateList" />
          </template>
        </el-input>
      </div>

      <el-table :data="jobTemplateList" border @sort-change="onJobTemplateSortChange"
      >
        <el-table-column prop="name" label="Name" min-width="300" show-overflow-tooltip sortable="custom"></el-table-column>

        <el-table-column label="Actions" width="160" align="center">
          <template #default="{$index, row}">
            <el-button v-if="!hasAdded(row)"
              text :icon="Plus" @click.stop="handleAddJobTemplate($index, row)">
              Add
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
          :pager-count="10"
          background
          layout="total, sizes, prev, pager, next, jumper"
          v-model:current-page="table2PageNum"
          v-model:page-size="table2PageSize"
          :page-sizes="pageSizeList"
          :total="totalJobTemplates"

        ></el-pagination>
      </div>
    </div>
    <div class="toolbar mt-4">
      <div class="spacer"></div>
      <el-button
        class="toolbar-button"
        type="primary" @click="emit('ok')">
        Ok
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onBeforeMount, watch, computed } from 'vue';
import type { PropType } from 'vue';

import { Search, Plus } from '@element-plus/icons-vue';

import { gFavoriteFolderApi, gJobTemplateApi } from '@/api';
import { gNotificationService } from '@/services';

import type { PmsFavoriteFolder, PmsJobTemplate, PmsJobTemplateQueryParam, PmsFavoriteFolderTemplateRelation } from '@/model/dto/pms';

import { useProjectStore } from '@/stores/project';

const props = defineProps({
  favoriteFolder: {
    type: Object as PropType<PmsFavoriteFolder>,
    required: true,
  },
  oldTemplateList: {
    type: Object as PropType<Array<PmsJobTemplate>>,
    required: true,
  }
})

const emit = defineEmits(['ok']);

const projectStore = useProjectStore();

const handling = ref(false);
const defaultPageSize = 10;
const pageSizeList = [10, 5];

const addedTemplateList = ref<Array<PmsJobTemplate>>([]);
const newTemplateList = computed(() => {
  const targetTemplateList = [...props.oldTemplateList, ...addedTemplateList.value];
  return targetTemplateList;
})

//#region Job Template List
const loadingTemplate = ref(0);
let loadingTemplateLastReqTime;
const jobTemplateList = ref<Array<PmsJobTemplate>>([]);
const totalJobTemplates = ref(0);

const keywordTemplate = ref('');
const table2PageNum = ref(1);
const table2PageSize = ref(defaultPageSize);
let table2SortBy = '';
let table2SortDesc: boolean | undefined = undefined;

watch([table2PageNum, table2PageSize], () => {
  refreshJobTemplateList();
})

onBeforeMount(() => {
  refreshJobTemplateList();
})

async function refreshJobTemplateList() {
  const reqTime = Date.now();
  loadingTemplateLastReqTime = reqTime;

  console.log(`refreshJobTemplateList`);

  try {
    loadingTemplate.value += 1;

    const queryParam: PmsJobTemplateQueryParam = {
      projectId: projectStore.selectedProjectBase!.id,
      pageNum: table2PageNum.value,
      pageSize: table2PageSize.value,
    };

    keywordTemplate.value && (queryParam.keyword = keywordTemplate.value);

    if (table2SortBy) {
      queryParam.sortBy = table2SortBy;
      queryParam.sortDesc = table2SortDesc;
    }

    const rsp = await gJobTemplateApi.getJobTemplateList(queryParam);

    if (loadingTemplateLastReqTime !== reqTime) {
      return;
    }

    jobTemplateList.value = rsp.data?.list || [];
    totalJobTemplates.value = rsp.data?.total || 0;
  } finally {
    loadingTemplate.value -= 1;
  }
}

async function onJobTemplateSortChange({ column, prop, order }: any) {
  if (!order) {
    table2SortDesc = undefined;
  } else if (order === 'ascending') {
    table2SortDesc = false;
  } else if (order === 'descending') {
    table2SortDesc = true;
  } else {
    throw new Error(`unexpected order ${order}`)
  }

  if (table2SortDesc == null) {
    table2SortBy = '';
  } else {
    if (prop) {
      table2SortBy = prop;
    } else {
      throw new Error(`unexpected column ${column.label}`);
    }
  }

  console.log(`table2SortBy ${table2SortBy}, table2SortDesc ${table2SortDesc}`);

  await refreshJobTemplateList();
}
//#endregion

//#region Job Template Item
function hasAdded(row: PmsJobTemplate) {
  const found = newTemplateList.value.find((item) => {
    if (item.id === row.id) {
      return true;
    }
    return false;
  })

  return !!found;
}

async function handleAddJobTemplate(index: number, row: PmsJobTemplate) {
  console.log(`handleAddJobTemplate: index ${index}, row ${row.name}`);

  try {
    handling.value = true;

    const data: PmsFavoriteFolderTemplateRelation = {
      folderId: props.favoriteFolder.id,
      templateId: row.id,
    };

    const rsp = await gFavoriteFolderApi.addFolderTemplate(data);

    if (rsp.error) {
      gNotificationService.error(rsp.error.message);
    } else {
      addedTemplateList.value.push(row);
    }
  } finally {
    handling.value = false;
  }
}
//#endregion
</script>

<style lang="scss" scoped>
.container {
  width: 100%;

  .toolbar {
    margin-bottom: 8px;
    display: flex;
    flex-wrap: wrap;
    align-items: center;

    .toolbar-button {
      min-width: 80px;
    }
  }

  .breadcrumb-container {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
  }

  .red {
    color: red;
  }

  .pagination {
    margin-top: 10px;
    display: flex;
  }

  .table-search-field {
    width: 300px;
  }
}
</style>
