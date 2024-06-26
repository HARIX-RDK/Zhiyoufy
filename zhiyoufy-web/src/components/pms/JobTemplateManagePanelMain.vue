<template>
  <div class="container">
    <div class="toolbar">
      <el-checkbox v-model="flatFolder" label="Flat Folder" />
      <el-checkbox v-model="flatTemplate" label="Flat Template"/>
    </div>

    <div v-if="!flatFolder" class="breadcrumb-container">
      <span
          class="font-weight-bold"
        >
          Breakcrumbs
        </span>

        <el-button
          :disabled="disableRootBreadcrumb"
          @click="onClickRootBreadcrumb"
          text
        >
          Root
        </el-button>

        <template
          v-for="item in folderBreadcrumbs"
          :key="item.id"
        >
          <span>
            /
          </span>

          <el-button
            :disabled="lastBreadcrumb(item)"
            @click="onClickBreadcrumb(item)"
            text
          >
            {{ item.name }}
          </el-button>
        </template>
    </div>

    <div class="table-container">
      <div class="job-folder-table-container">
        <div class="toolbar">
          <el-input v-model="keywordFolder" placeholder="search keyword" class="table-search-field">
            <template #append>
              <el-button :icon="Search" @click="refreshJobFolderList" />
            </template>
          </el-input>

          <div class="spacer"></div>

          <el-button v-if="!flatFolder"
            :icon="Plus" @click="onNewJobFolder">New JobFolder
          </el-button>
        </div>

        <el-table :data="jobFolderList" border @sort-change="onJobFolderSortChange"
          @row-click="onClickJobFolder" @row-dblclick="onDoubleClickJobFolder"
          highlight-current-row row-key="id" :current-row-key="selectedJobFolder?.id || -1"
        >
          <el-table-column prop="name" label="Name" min-width="300" show-overflow-tooltip sortable="custom"></el-table-column>

          <el-table-column label="Actions" width="110" align="center">
            <template #default="scope">
              <el-button
                text :icon="Edit" @click.stop="handleEditJobFolder(scope.$index, scope.row)">
                Edit
              </el-button>
            </template>
          </el-table-column>

          <el-table-column type="expand">
            <template #default="{ $index, row }">
              <div class="p-4">
                <span>More Actions: </span>
                <el-button
                  text :icon="Delete" class="red" @click="handleDeleteJobFolder($index, row)">
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
            :small="table1Small"
            :pager-count="5"
            background
            layout="total, sizes, prev, pager, next, jumper"
            v-model:current-page="table1PageNum"
            v-model:page-size="table1PageSize"
            :page-sizes="pageSizeList"
            :total="totalJobFolders"

          ></el-pagination>
        </div>
      </div>
      <div class="job-template-table-container">
        <div class="toolbar">
          <el-input v-model="keywordTemplate" placeholder="search keyword" class="table-search-field">
            <template #append>
              <el-button :icon="Search" @click="refreshJobTemplateList" />
            </template>
          </el-input>

          <div class="spacer"></div>

          <el-button v-if="selectedJobFolder"
            :icon="Plus" @click="onNewJobTemplate">New Template
          </el-button>
        </div>

        <el-table :data="jobTemplateList" border @sort-change="onJobTemplateSortChange"
          @row-click="onClickJobTemplate"
          highlight-current-row row-key="id" :current-row-key="selectedJobTemplate?.id || -1"
        >
          <el-table-column prop="name" label="Name" min-width="300" show-overflow-tooltip sortable="custom"></el-table-column>

          <el-table-column label="Actions" width="110" align="center">
            <template #default="scope">
              <el-button
                text :icon="Edit" @click.stop="handleEditJobTemplate(scope.$index, scope.row)">
                Edit
              </el-button>
            </template>
          </el-table-column>

          <el-table-column type="expand">
            <template #default="{ $index, row }">
              <div class="p-4">
                <span>More Actions: </span>
                <el-button
                  text :icon="Delete" class="red" @click="handleDeleteJobTemplate($index, row)">
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
            :small="table2Small"
            :pager-count="5"
            background
            layout="total, sizes, prev, pager, next, jumper"
            v-model:current-page="table2PageNum"
            v-model:page-size="table2PageSize"
            :page-sizes="pageSizeList"
            :total="totalJobTemplates"

          ></el-pagination>
        </div>
      </div>
    </div>

    <el-dialog
      v-if="jobFolderFormDialogVisible"
      v-model="jobFolderFormDialogVisible"
      :title="jobFolderFormDialogTitle"
    >
      <JobFolderForm
        :project-base="projectStore.selectedProjectBase!"
        :parent-job-folder="parentFolder"
        :jobFolder="editJobFolder"
        @ok="onJobFolderFormDialogOk" @cancel="jobFolderFormDialogVisible = false">
      </JobFolderForm>
    </el-dialog>

    <el-dialog
      v-if="jobTemplateFormDialogVisible"
      v-model="jobTemplateFormDialogVisible"
      :title="jobTemplateFormDialogTitle"
      fullscreen
    >
      <JobTemplateForm
        :project-base="projectStore.selectedProjectBase!"
        :job-folder="selectedJobFolder"
        :jobTemplate="editJobTemplate"
        @ok="onJobTemplateFormDialogOk" @cancel="jobTemplateFormDialogVisible = false">
      </JobTemplateForm>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onBeforeMount, watch, computed } from 'vue';

import { Search, Edit, Delete, Plus } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';

import JobFolderForm from '@/components/pms/JobFolderForm.vue';
import JobTemplateForm from '@/components/pms/JobTemplateForm.vue';

import { gJobFolderApi, gJobTemplateApi } from '@/api';

import type { PmsJobFolder, PmsJobFolderQueryParam,
  PmsJobTemplate, PmsJobTemplateQueryParam } from '@/model/dto/pms';

import { useProjectStore } from '@/stores/project';

const emit = defineEmits(['selected-job-template']);

const projectStore = useProjectStore();

const handling = ref(false);
const defaultPageSize = 9;
const pageSizeList = [9, 5];

//#region Job Folder Breadcrumb
const rootFolder = {
  id: 0,
  name: 'root',
} as PmsJobFolder;

const folderBreadcrumbs = ref<Array<PmsJobFolder>>([]);

watch(() => folderBreadcrumbs.value.length, () => {
  selectedJobFolder.value = undefined;

  refreshJobFolderList();
})

const disableRootBreadcrumb = computed(() => {
  if (folderBreadcrumbs.value.length) {
    return false;
  }
  return true;
})

const parentFolder = computed(() => {
  if (folderBreadcrumbs.value.length) {
    return folderBreadcrumbs.value[folderBreadcrumbs.value.length - 1];
  }
  return rootFolder;
})

async function onClickRootBreadcrumb() {
  folderBreadcrumbs.value = [];
}

async function onClickBreadcrumb(item: PmsJobFolder) {
  let index = folderBreadcrumbs.value.indexOf(item);
  folderBreadcrumbs.value = folderBreadcrumbs.value.slice(0, index+1);
}

function lastBreadcrumb(item: PmsJobFolder) {
  let index = folderBreadcrumbs.value.indexOf(item);

  if (index === folderBreadcrumbs.value.length - 1) {
    return true;
  }
  return false;
}
//#endregion

//#region Job Folder List
const flatFolder = ref(false);
const loadingFolder = ref(false);
const jobFolderList = ref<Array<PmsJobFolder>>([]);
const totalJobFolders = ref(0);

const keywordFolder = ref('');
const table1PageNum = ref(1);
const table1PageSize = ref(defaultPageSize);
const table1Small = ref(false);
let table1SortBy = '';
let table1SortDesc: boolean | undefined = undefined;

onBeforeMount(() => {
  refreshJobFolderList();
})

watch(flatFolder, () => {
  selectedJobFolder.value = undefined;

  refreshJobFolderList();
})

watch([table1PageNum, table1PageSize], () => {
  refreshJobFolderList();
})

watch(() => {
  const pageCount = (totalJobFolders.value + table1PageSize.value - 1) / table1PageSize.value;
  return pageCount;
}, (value) => {
  if (value > 3) {
    table1Small.value = true;
  } else {
    table1Small.value = false;
  }
})

async function refreshJobFolderList() {
  console.log(`refreshJobFolderList`);

  try {
    loadingFolder.value = true;

    const queryParam: PmsJobFolderQueryParam = {
      projectId: projectStore.selectedProjectBase!.id,
      pageNum: table1PageNum.value,
      pageSize: table1PageSize.value,
    };

    if (!flatFolder.value) {
      queryParam.parentId = parentFolder.value.id;
    }

    keywordFolder.value && (queryParam.keyword = keywordFolder.value);

    if (table1SortBy) {
      queryParam.sortBy = table1SortBy;
      queryParam.sortDesc = table1SortDesc;
    }

    const rsp = await gJobFolderApi.getJobFolderList(queryParam);

    jobFolderList.value = rsp.data?.list || [];
    totalJobFolders.value = rsp.data?.total || 0;
  } finally {
    loadingFolder.value= false;
  }
}

async function onJobFolderSortChange({ column, prop, order }: any) {
  if (!order) {
    table1SortDesc = undefined;
  } else if (order === 'ascending') {
    table1SortDesc = false;
  } else if (order === 'descending') {
    table1SortDesc = true;
  } else {
    throw new Error(`unexpected order ${order}`)
  }

  if (table1SortDesc == null) {
    table1SortBy = '';
  } else {
    if (prop) {
      table1SortBy = prop;
    } else {
      throw new Error(`unexpected column ${column.label}`);
    }
  }

  console.log(`table1SortBy ${table1SortBy}, table1SortDesc ${table1SortDesc}`);

  await refreshJobFolderList();
}
//#endregion

//#region Job Folder Item
const jobFolderFormDialogVisible = ref(false);
const jobFolderFormDialogTitle = ref('');
const editJobFolder = ref<PmsJobFolder>();
const selectedJobFolder = ref<PmsJobFolder>();

watch([jobFolderFormDialogVisible], ([value]) => {
  if (!value) {
    editJobFolder.value = undefined;
  }
});

function handleEditJobFolder(index: number, row: PmsJobFolder) {
  console.log(`handleEditJobFolder: index ${index}, row ${row.name}`);
  editJobFolder.value = row;
  jobFolderFormDialogTitle.value = `Edit JobFolder: ${row.name}`;
  jobFolderFormDialogVisible.value = true;
}

function onNewJobFolder() {
  jobFolderFormDialogTitle.value = 'New JobFolder';
  jobFolderFormDialogVisible.value = true;
}

function onJobFolderFormDialogOk() {
  jobFolderFormDialogVisible.value = false;
  refreshJobFolderList();
}

async function handleDeleteJobFolder(index: number, row: PmsJobFolder) {
  console.log(`handleDeleteJobFolder: index ${index}, row ${row.name}`);

  try {
    handling.value = true;

    const action = await ElMessageBox.confirm(
      `Are you sure to delete job folder ${row.name}. Continue?`,
      'Warning',
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
    );

    if (action === 'confirm') {
      await gJobFolderApi.delJobFolder(row.id);

      await refreshJobFolderList();
    }
  }
  finally {
    handling.value = false;
  }
}

async function onClickJobFolder(row: PmsJobFolder) {
  console.log(`onClickJobFolder: row ${row.name}`);

  if (loadingFolder.value) {
    console.log(`onClickJobFolder: ignore when loading folder`);
    return;
  }

  selectedJobFolder.value = row;
}

async function onDoubleClickJobFolder(row: PmsJobFolder, column: any) {
  if (flatFolder.value) {
    return;
  }

  if (column.label !== 'Name') {
    return;
  }

  console.log(`onDoubleClickJobFolder: row ${row.name}`);
  folderBreadcrumbs.value.push(row);
}
//#endregion

//#region Job Template List
const flatTemplate = ref(false);
const loadingTemplate = ref(0);
let loadingTemplateLastReqTime;
const jobTemplateList = ref<Array<PmsJobTemplate>>([]);
const totalJobTemplates = ref(0);

const keywordTemplate = ref('');
const table2PageNum = ref(1);
const table2PageSize = ref(defaultPageSize);
const table2Small = ref(false);
let table2SortBy = '';
let table2SortDesc: boolean | undefined = undefined;

watch(flatTemplate, () => {
  selectedJobTemplate.value = undefined;

  refreshJobTemplateList();
})

watch([selectedJobFolder], () => {
  if (!flatTemplate.value) {
    resetJobTemplateData();

    refreshJobTemplateList();
  }
})

function resetJobTemplateData() {
  selectedJobTemplate.value = undefined;
  jobTemplateList.value = [];
  totalJobTemplates.value = 0;
}

watch([table2PageNum, table2PageSize], () => {
  refreshJobTemplateList();
})

watch(() => {
  const pageCount = (totalJobTemplates.value + table2PageSize.value - 1) / table2PageSize.value;
  return pageCount;
}, (value) => {
  if (value > 3) {
    table2Small.value = true;
  } else {
    table2Small.value = false;
  }
})

async function refreshJobTemplateList() {
  const reqTime = Date.now();
  loadingTemplateLastReqTime = reqTime;

  if (!flatTemplate.value && !selectedJobFolder.value) {
    jobTemplateList.value = [];
    totalJobTemplates.value = 0;
    return;
  }

  console.log(`refreshJobTemplateList`);

  try {
    loadingTemplate.value += 1;

    const queryParam: PmsJobTemplateQueryParam = {
      projectId: projectStore.selectedProjectBase!.id,
      pageNum: table2PageNum.value,
      pageSize: table2PageSize.value,
    };

    if (!flatTemplate.value) {
      queryParam.folderId = selectedJobFolder.value!.id;
    }

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
const jobTemplateFormDialogVisible = ref(false);
const jobTemplateFormDialogTitle = ref('');
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

function handleEditJobTemplate(index: number, row: PmsJobTemplate) {
  console.log(`handleEditJobTemplate: index ${index}, row ${row.name}`);
  editJobTemplate.value = row;
  jobTemplateFormDialogTitle.value = `Edit JobTemplate: ${row.name}`;
  jobTemplateFormDialogVisible.value = true;
}

function onNewJobTemplate() {
  jobTemplateFormDialogTitle.value = 'New JobTemplate';
  jobTemplateFormDialogVisible.value = true;
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
      `Are you sure to delete job folder ${row.name}. Continue?`,
      'Warning',
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
    );

    if (action === 'confirm') {
      await gJobTemplateApi.delJobTemplate(row.id);

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

  .job-folder-table-container {
    display: inline-block;
    vertical-align: top;
    width: 500px;
  }

  .job-template-table-container {
    display: inline-block;
    vertical-align: top;
    // sidebar: 251px folder table: 500px margin&padding: 29px
    width: calc(var(--window-width) - 900px);
    min-width: 600px;
    margin-left: 10px;
    min-height: 300px;
  }
}
</style>
