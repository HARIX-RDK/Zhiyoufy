<template>
  <div class="container">
    <div class="table-container">
      <div class="favorite-folder-table-container">
        <div class="toolbar">
          <el-input v-model="keywordFolder" placeholder="search keyword" class="table-search-field">
          </el-input>

          <div class="spacer"></div>

          <el-button
            :icon="Plus" @click="onNewFavoriteFolder">New FavoriteFolder
          </el-button>
        </div>

        <el-table :data="favoriteFolderListPage" border @sort-change="onFavoriteFolderSortChange"
          @row-click="onClickFavoriteFolder"
          highlight-current-row row-key="id" :current-row-key="selectedFavoriteFolder?.id || -1"
        >
          <el-table-column prop="name" label="Name" min-width="300" show-overflow-tooltip sortable="custom"></el-table-column>

          <el-table-column label="Actions" width="110" align="center">
            <template #default="scope">
              <el-button
                text :icon="Edit" @click.stop="handleEditFavoriteFolder(scope.$index, scope.row)">
                Edit
              </el-button>
            </template>
          </el-table-column>

          <el-table-column type="expand">
            <template #default="{ $index, row }">
              <div class="p-4">
                <span>More Actions: </span>
                <el-button
                  text :icon="Delete" class="red" @click="handleDeleteFavoriteFolder($index, row)">
                  Delete
                </el-button>

                <hr>

                <div class="my-1">
                  ModifiedTime: {{ row.modifiedTime }}
                </div>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <div class="spacer"></div>
          <el-pagination
            :pager-count="5"
            background
            layout="prev, pager, next"
            v-model:current-page="table1PageNum"
            v-model:page-size="table1PageSize"
            :page-sizes="pageSizeList"
            :total="totalFavoriteFolders"

          ></el-pagination>
        </div>
        <div class="pagination">
          <div class="spacer"></div>
          <el-pagination
            :pager-count="5"
            background
            layout="total, sizes, jumper"
            v-model:current-page="table1PageNum"
            v-model:page-size="table1PageSize"
            :page-sizes="pageSizeList"
            :total="totalFavoriteFolders"

          ></el-pagination>
        </div>
      </div>

      <div class="job-template-table-container">
        <div class="toolbar">
          <el-input v-model="keywordTemplate" placeholder="search keyword" class="table-search-field">
          </el-input>

          <div class="spacer"></div>

          <el-button v-if="selectedFavoriteFolder"
            :icon="Plus" @click="onAddJobTemplate">Add Template
          </el-button>
        </div>

        <el-table :data="jobTemplateListPage" border @sort-change="onJobTemplateSortChange"
          @row-click="onClickJobTemplate"
          highlight-current-row row-key="id" :current-row-key="selectedJobTemplate?.id || -1"
        >
          <el-table-column prop="name" label="Name" min-width="300" show-overflow-tooltip sortable="custom"></el-table-column>

          <el-table-column label="Actions" width="160" align="center">
            <template #default="{ $index, row }">
              <el-button
                text :icon="Delete" @click="handleDeleteJobTemplate($index, row)">
                Delete
              </el-button>
            </template>
          </el-table-column>

          <el-table-column type="expand">
            <template #default="{ $index, row }">
              <div class="p-4">
                <span>More Actions: </span>
                <el-button
                  text :icon="Edit" @click.stop="handleEditJobTemplate($index, row)">
                  Edit
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
            :pager-count="5"
            background
            layout="prev, pager, next"
            v-model:current-page="table2PageNum"
            v-model:page-size="table2PageSize"
            :page-sizes="pageSizeList"
            :total="totalJobTemplates"

          ></el-pagination>
        </div>
        <div class="pagination">
          <div class="spacer"></div>
          <el-pagination
            :pager-count="5"
            background
            layout="total, sizes, jumper"
            v-model:current-page="table2PageNum"
            v-model:page-size="table2PageSize"
            :page-sizes="pageSizeList"
            :total="totalJobTemplates"

          ></el-pagination>
        </div>
      </div>
    </div>

    <el-dialog
      v-if="favoriteFolderFormDialogVisible"
      v-model="favoriteFolderFormDialogVisible"
      :title="favoriteFolderFormDialogTitle"
    >
      <FavoriteFolderForm
        :project-base="projectStore.selectedProjectBase!"
        :favoriteFolder="editFavoriteFolder"
        @ok="onFavoriteFolderFormDialogOk" @cancel="favoriteFolderFormDialogVisible = false">
      </FavoriteFolderForm>
    </el-dialog>

    <el-dialog
      v-if="jobTemplateFormDialogVisible"
      v-model="jobTemplateFormDialogVisible"
      :title="jobTemplateFormDialogTitle"
      fullscreen
    >
      <JobTemplateForm
        :project-base="projectStore.selectedProjectBase!"
        :jobTemplate="editJobTemplate"
        @ok="onJobTemplateFormDialogOk" @cancel="jobTemplateFormDialogVisible = false">
      </JobTemplateForm>
    </el-dialog>

    <el-dialog
      v-if="favoriteAddTemplateDialogVisible"
      v-model="favoriteAddTemplateDialogVisible"
      :title="favoriteAddTemplateDialogTitle"
      fullscreen
    >
      <FavoriteAddTemplateDialog
        @ok="favoriteAddTemplateDialogVisible = false" @cancel="favoriteAddTemplateDialogVisible = false"
        :old-template-list="jobTemplateListAll" :favorite-folder="selectedFavoriteFolder!">
      </FavoriteAddTemplateDialog>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onBeforeMount, watch, computed } from 'vue';

import { Edit, Delete, Plus } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';

import FavoriteFolderForm from '@/components/pms/FavoriteFolderForm.vue';
import JobTemplateForm from '@/components/pms/JobTemplateForm.vue';
import FavoriteAddTemplateDialog from '@/components/pms/FavoriteAddTemplateDialog.vue';

import { gFavoriteFolderApi } from '@/api';

import type { PmsFavoriteFolder, PmsJobTemplate, PmsFavoriteFolderTemplateRelation } from '@/model/dto/pms';

import { useProjectStore } from '@/stores/project';

const emit = defineEmits(['selected-job-template']);

const projectStore = useProjectStore();

const handling = ref(false);
const defaultPageSize = 9;
const pageSizeList = [9, 5];

//#region Favorite Folder List
const loadingFolder = ref(false);
const keywordFolder = ref('');
const table1PageNum = ref(1);
const table1PageSize = ref(defaultPageSize);
const table1SortBy = ref('');
const table1SortDesc = ref<boolean | undefined>();

const favoriteFolderListAll = ref<Array<PmsFavoriteFolder>>([]);
const favoriteFolderListProcessed = computed(() => {
  let targetFavoriteFolderList = [...favoriteFolderListAll.value];

  if (keywordFolder.value) {
    targetFavoriteFolderList = targetFavoriteFolderList.filter((item) => {
      if (item.name.includes(keywordFolder.value)) {
        return true;
      }
      return false;
    })
  }

  if (table1SortBy.value) {
    targetFavoriteFolderList.sort((a, b) => {
      const prop = table1SortBy.value as keyof PmsFavoriteFolder;
      if (a[prop] < b[prop]) {
        if (!table1SortDesc.value) {
          return -1;
        } else {
          return 1;
        }
      } else if (a[prop] > b[prop]) {
        if (!table1SortDesc.value) {
          return 1;
        } else {
          return -1;
        }
      } else {
        return 0;
      }
    })
  }

  return targetFavoriteFolderList;
});
const totalFavoriteFolders = computed(() => {
  return favoriteFolderListProcessed.value.length;
});
const favoriteFolderListPage = computed(() => {
  const start = (table1PageNum.value - 1) * table1PageSize.value;
  const end = table1PageNum.value * table1PageSize.value;
  let targetFavoriteFolderList = favoriteFolderListProcessed.value.slice(start, end);
  return targetFavoriteFolderList;
});

onBeforeMount(() => {
  refreshFavoriteFolderList();
})

async function refreshFavoriteFolderList() {
  console.log(`refreshFavoriteFolderList`);

  try {
    loadingFolder.value = true;

    const rsp = await gFavoriteFolderApi.getFavoriteFolderList(projectStore.selectedProjectBase!.id);

    favoriteFolderListAll.value = rsp.data || [];
  } finally {
    loadingFolder.value= false;
  }
}

async function onFavoriteFolderSortChange({ column, prop, order }: any) {
  if (!order) {
    table1SortDesc.value = undefined;
  } else if (order === 'ascending') {
    table1SortDesc.value = false;
  } else if (order === 'descending') {
    table1SortDesc.value = true;
  } else {
    throw new Error(`unexpected order ${order}`)
  }

  if (table1SortDesc.value == null) {
    table1SortBy.value = '';
  } else {
    if (prop) {
      table1SortBy.value = prop;
    } else {
      throw new Error(`unexpected column ${column.label}`);
    }
  }

  console.log(`table1SortBy ${table1SortBy.value}, table1SortDesc ${table1SortDesc.value}`);
}
//#endregion

//#region Favorite Folder Item
const favoriteFolderFormDialogVisible = ref(false);
const favoriteFolderFormDialogTitle = ref('');
const editFavoriteFolder = ref<PmsFavoriteFolder>();
const selectedFavoriteFolder = ref<PmsFavoriteFolder>();

watch([favoriteFolderFormDialogVisible], ([value]) => {
  if (!value) {
    editFavoriteFolder.value = undefined;
  }
});

function handleEditFavoriteFolder(index: number, row: PmsFavoriteFolder) {
  console.log(`handleEditFavoriteFolder: index ${index}, row ${row.name}`);
  editFavoriteFolder.value = row;
  favoriteFolderFormDialogTitle.value = `Edit FavoriteFolder: ${row.name}`;
  favoriteFolderFormDialogVisible.value = true;
}

function onNewFavoriteFolder() {
  favoriteFolderFormDialogTitle.value = 'New FavoriteFolder';
  favoriteFolderFormDialogVisible.value = true;
}

function onFavoriteFolderFormDialogOk() {
  favoriteFolderFormDialogVisible.value = false;
  refreshFavoriteFolderList();
}

async function handleDeleteFavoriteFolder(index: number, row: PmsFavoriteFolder) {
  console.log(`handleDeleteFavoriteFolder: index ${index}, row ${row.name}`);

  try {
    handling.value = true;

    const action = await ElMessageBox.confirm(
      `Are you sure to delete favorite folder ${row.name}. Continue?`,
      'Warning',
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
    );

    if (action === 'confirm') {
      await gFavoriteFolderApi.delFavoriteFolder(row.id);

      await refreshFavoriteFolderList();
    }
  }
  finally {
    handling.value = false;
  }
}

async function onClickFavoriteFolder(row: PmsFavoriteFolder) {
  console.log(`onClickFavoriteFolder: row ${row.name}`);

  selectedFavoriteFolder.value = row;
}

watch(favoriteFolderListPage, () => {
  if (!selectedFavoriteFolder.value) {
    return;
  }

  const found = favoriteFolderListPage.value.find((item) => {
    if (item.id === selectedFavoriteFolder.value?.id) {
      return true;
    }
    return false;
  })

  if (!found) {
    selectedFavoriteFolder.value = undefined;
  }
})
//#endregion

//#region Job Template List
const loadingTemplate = ref(0);
let loadingTemplateLastReqTime;
const keywordTemplate = ref('');
const table2PageNum = ref(1);
const table2PageSize = ref(defaultPageSize);
const table2SortBy = ref('');
const table2SortDesc = ref<boolean | undefined>();

watch([selectedFavoriteFolder], () => {
  refreshJobTemplateList();
})

const jobTemplateListAll = ref<Array<PmsJobTemplate>>([]);
const jobTemplateListProcessed = computed(() => {
  let targetJobTemplateList = [...jobTemplateListAll.value];

  if (keywordTemplate.value) {
    targetJobTemplateList = targetJobTemplateList.filter((item) => {
      if (item.name.includes(keywordTemplate.value)) {
        return true;
      }
      return false;
    })
  }

  if (table2SortBy.value) {
    targetJobTemplateList.sort((a, b) => {
      const prop = table2SortBy.value as keyof PmsJobTemplate;
      if (a[prop] < b[prop]) {
        if (!table2SortDesc.value) {
          return -1;
        } else {
          return 1;
        }
      } else if (a[prop] > b[prop]) {
        if (!table2SortDesc.value) {
          return 1;
        } else {
          return -1;
        }
      } else {
        return 0;
      }
    })
  }

  return targetJobTemplateList;
});
const totalJobTemplates = computed(() => {
  return jobTemplateListProcessed.value.length;
});
const jobTemplateListPage = computed(() => {
  const start = (table2PageNum.value - 1) * table2PageSize.value;
  const end = table2PageNum.value * table2PageSize.value;
  let targetJobTemplateList = jobTemplateListProcessed.value.slice(start, end);
  return targetJobTemplateList;
});

async function refreshJobTemplateList() {
  const reqTime = Date.now();
  loadingTemplateLastReqTime = reqTime;

  if (!selectedFavoriteFolder.value) {
    jobTemplateListAll.value = [];
    return;
  }

  console.log(`refreshJobTemplateList`);

  try {
    loadingTemplate.value += 1;

    const rsp = await gFavoriteFolderApi.getFolderTemplateList(selectedFavoriteFolder.value.id);

    if (loadingTemplateLastReqTime !== reqTime) {
      return;
    }

    jobTemplateListAll.value = rsp.data || [];
  } finally {
    loadingTemplate.value -= 1;
  }
}

async function onJobTemplateSortChange({ column, prop, order }: any) {
  if (!order) {
    table2SortDesc.value = undefined;
  } else if (order === 'ascending') {
    table2SortDesc.value = false;
  } else if (order === 'descending') {
    table2SortDesc.value = true;
  } else {
    throw new Error(`unexpected order ${order}`)
  }

  if (table2SortDesc.value == null) {
    table2SortBy.value = '';
  } else {
    if (prop) {
      table2SortBy.value = prop;
    } else {
      throw new Error(`unexpected column ${column.label}`);
    }
  }

  console.log(`table2SortBy ${table2SortBy.value}, table2SortDesc ${table2SortDesc.value}`);
}
//#endregion

//#region Job Template Item
const jobTemplateFormDialogVisible = ref(false);
const jobTemplateFormDialogTitle = ref('');
const favoriteAddTemplateDialogVisible = ref(false);
const favoriteAddTemplateDialogTitle = ref('');
const editJobTemplate = ref<PmsJobTemplate>();
const selectedJobTemplate = ref<PmsJobTemplate>();

watch(selectedJobTemplate, (value) => {
  emit('selected-job-template', value);
});

watch([jobTemplateFormDialogVisible], ([value]) => {
  if (!value) {
    editJobTemplate.value = undefined;
  }
});

watch([favoriteAddTemplateDialogVisible], ([value]) => {
  if (!value) {
    refreshJobTemplateList();
  }
})

function handleEditJobTemplate(index: number, row: PmsJobTemplate) {
  console.log(`handleEditJobTemplate: index ${index}, row ${row.name}`);
  editJobTemplate.value = row;
  jobTemplateFormDialogTitle.value = `Edit JobTemplate: ${row.name}`;
  jobTemplateFormDialogVisible.value = true;
}

function onAddJobTemplate() {
  favoriteAddTemplateDialogTitle.value = 'Add JobTemplate';
  favoriteAddTemplateDialogVisible.value = true;
}

function onJobTemplateFormDialogOk() {
  jobTemplateFormDialogVisible.value = false;
  refreshJobTemplateList();
}

async function handleDeleteJobTemplate(index: number, row: PmsJobTemplate) {
  console.log(`handleDeleteJobTemplate: index ${index}, row ${row.name}`);

  try {
    handling.value = true;

    const action = await ElMessageBox.confirm(
      `Are you sure to delete job template ${row.name}. Continue?`,
      'Warning',
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
    );

    const data: PmsFavoriteFolderTemplateRelation = {
      folderId: selectedFavoriteFolder.value!.id,
      templateId: row.id,
    }

    if (action === 'confirm') {
      await gFavoriteFolderApi.delFolderTemplate(data);

      await refreshJobTemplateList();
    }
  }
  finally {
    handling.value = false;
  }
}

function onClickJobTemplate(row: PmsJobTemplate) {
  console.log(`onClickJobTemplate: row ${row.name}`);

  selectedJobTemplate.value = row;
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

  .favorite-folder-table-container {
    display: inline-block;
    vertical-align: top;
    width: 500px;
  }

  .job-template-table-container {
    display: inline-block;
    vertical-align: top;
    // sidebar: 251px folder table: 600px margin&padding: 29px
    width: calc(var(--window-width) - 900px);
    min-width: 600px;
    margin-left: 10px;
    min-height: 300px;
  }
}
</style>
