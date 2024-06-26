<template>
  <div class="component-container">
    <div class="dialog-content">
      <div class="toolbar">
        <div class="spacer"></div>

        <el-button type="primary" :icon="Search" @click="refreshJobChildRunResultList">
          Refresh
        </el-button>
      </div>

      <el-table :data="jobChildRunResultList" border class="table">
        <el-table-column prop="environmentName" label="Environment" width="120"></el-table-column>
        <el-table-column prop="templateName" label="JobTemplate" width="200"></el-table-column>
        <el-table-column prop="index" label="Index" width="60"></el-table-column>
        <el-table-column prop="endReason" label="EndReason" width="170"></el-table-column>
        <el-table-column prop="endOk" label="EndOk" width="90"></el-table-column>
        <el-table-column prop="resultOk" label="ResultOk" width="90"></el-table-column>
        <el-table-column prop="passed" label="Passed" width="80" align="center">
          <template #default="{ row }">
            <el-tag effect="dark" :type="getRunResultType(row)">
              {{ row.passed }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="timestamp" label="EndedOn" width="160">
          <template #default="scope">
            {{ TimeUtils.displayTimeUptoSecs(scope.row.timestamp) }}
          </template>
        </el-table-column>

        <el-table-column label="JobOutputUrl" min-width="300">
          <template #default="{ row }">
            <a :href="row.jobOutputUrl" target="_blank">
              {{ row.jobOutputUrl }}
            </a>
          </template>
        </el-table-column>

        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="p-4">
              <div class="my-1">
                runGuid: {{ row.runGuid }}
              </div>
              <div class="my-1">
                EndedOn: {{ row.timestamp }}
              </div>
              <div class="my-1 respect-line-change">
                EndDetail: {{ row.endDetail }}
              </div>
              <div class="my-1 respect-line-change">
                ChildStatusCntMap: {{ row.childStatusCntMap }}
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
          :total="totalJobChildRunResults"

        ></el-pagination>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onBeforeMount  } from 'vue';
import type { PropType } from 'vue';

import { Search } from '@element-plus/icons-vue';

import type { JmsJobRunResultFull, JmsActiveJobRunBase,
  JmsJobChildRunResultQueryParam, JmsJobChildRunResultFull } from "@/model/dto/jms";

import { gJobRunApi } from '@/api';
import { TimeUtils } from '@/utils';

const props = defineProps({
  jobRun: {
    type: Object as PropType<JmsJobRunResultFull | JmsActiveJobRunBase>,
    required: true,
  }
});

const logPrefix = 'FinishedJobChildRunDialog:';

onBeforeMount(() => {
  refreshJobChildRunResultList();
})

//#region JobChildRunResult List
const loading = ref(false);
const pageNum = ref(1);
const pageSize = ref(10);
const jobChildRunResultList = ref([] as JmsJobChildRunResultFull[]);
const totalJobChildRunResults = ref(0);

watch([pageNum, pageSize], () => {
  refreshJobChildRunResultList();
})

async function refreshJobChildRunResultList() {
  console.log(`${logPrefix} refreshJobChildRunResultList`);

  try {
    loading.value = true;

    const queryParam: JmsJobChildRunResultQueryParam = {
      runGuid: props.jobRun.runGuid,
      environmentId: props.jobRun.environmentId,
      projectId: props.jobRun.projectId,
      templateId: props.jobRun.templateId,
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    };

    const rsp = await gJobRunApi.getJobChildRunResultList(queryParam);

    jobChildRunResultList.value = rsp.data?.list || [];
    totalJobChildRunResults.value = rsp.data?.total || 0;
  } finally {
    loading.value= false;
  }
}
//#endregion

//#region JobChildRunResult List Item
function getRunResultType(row: JmsJobChildRunResultFull) {
  if (row.passed) {
    return 'success';
  }
  return 'danger';
}
//#endregion
</script>

<style lang="scss" scoped>
.component-container {
  width: 100%;
  height: 100%;

  .toolbar {
    margin-bottom: 16px;
    display: flex;
    flex-wrap: wrap;
    align-items: center;
  }

  .pagination {
    margin-top: 10px;
    display: flex;
  }

  .respect-line-change {
    white-space: pre-wrap;
  }
}
</style>
