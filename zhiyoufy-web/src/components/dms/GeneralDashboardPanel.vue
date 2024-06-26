<template>
  <div class="container">
    <div class="toolbar">
      <span>
        Environment
      </span>
      <el-select
        class="ml-1 input"
        v-model="environmentName"
        filterable
      >
        <el-option
          v-for="item in environmentList"
          :key="item.id"
          :label="item.name"
          :value="item.name"
        />
      </el-select>

      <span class="ml-2">Template</span>
      <el-input
        class="ml-1 template-input"
        v-model="templateName"
        placeholder="Template Name"
      >
      </el-input>

      <span class="ml-2">RunTag</span>
      <el-input
        class="ml-2 input"
        v-model="runTag"
        placeholder="Run Tag"
      >
      </el-input>

      <span class="ml-2"> Interval </span>
      <el-select
        class="ml-1 input"
        v-model="selectedCalendarIntervalValue"
      >
        <el-option
          v-for="item in supportedCalendarIntervals"
          :key="item.value"
          :label="item.name"
          :value="item.value"
        />
      </el-select>

      <div class="spacer"></div>

      <el-button
        type="primary" :icon="Refresh" @click="updateTotalRunsChart">
        Refresh
      </el-button>
    </div>

    <div class="summary-line">
       <div>
         <span>Total Runs: </span>
         <span class="ml-1">{{ totalRuns }}</span>
       </div>

       <div class="ml-8">
         <span>Total Passed Runs: </span>
         <span class="ml-1">{{ totalPassedRuns }}</span>
       </div>

       <div class="ml-8">
         <span>Total NotPassed Runs: </span>
         <span class="ml-1">{{ totalNotPassedRuns }}</span>
       </div>
    </div>

    <div class="chart-container">
        <canvas id="general-total-runs-chart">
        </canvas>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onBeforeMount, watch, computed } from 'vue';
import type { PropType } from 'vue';

import { Refresh } from '@element-plus/icons-vue';

import { Chart } from 'chart.js';
import { v4 as uuidv4 } from 'uuid';
import dayjs from 'dayjs';

import type { TimeRange } from '@/model/datetime-range';
import type { EmsEnvironmentBase } from '@/model/dto/ems';
import type { PmsProjectBase } from '@/model/dto/pms';

import { gElkAggApi } from '@/api/dms';
import { DateUtils, StrUtils } from '@/utils';
import { gNotificationService } from '@/services';

import { useEnvironmentStore } from '@/stores/environment';

const props = defineProps({
  selectedProject: {
    type: Object as PropType<PmsProjectBase>,
    required: true,
  },
  timeRange: {
    type: Object as PropType<TimeRange>,
    required: true,
  },
});

//#region Environment
const environmentStore = useEnvironmentStore();

environmentStore.refreshEnvironmentBaseList();

const environmentName = ref('all');
const selectedEnvironmentBase = ref<EmsEnvironmentBase>();

const allEnvironment = {
  id: 0,
  name: 'all',
} as EmsEnvironmentBase;

const environmentList = computed(() => {
  const targetEnvironmentList = [allEnvironment, ...environmentStore.environmentBaseList];
  return targetEnvironmentList;
})

watch(environmentName, (value) => {
  const found = environmentList.value.find((environmentBase) => {
    if (environmentBase.name === value) {
      return true;
    }
    return false;
  })
  selectedEnvironmentBase.value = found;
})
//#endregion

//#region TemplateName RunTag CalendarInterval
const supportedCalendarIntervals = [
  {
    name: '1 day',
    value: '1d',
    format: 'yyyy-MM-dd'
  },
  {
    name: '1 month',
    value: '1M',
    format: 'yyyy-MM',
  },
  {
    name: '1 year',
    value: '1y',
    format: 'yyyy',
  }
];

const templateName = ref('');
const runTag = ref('');
const selectedCalendarIntervalValue = ref('1d');
const selectedCalendarInterval = ref();

watch(selectedCalendarIntervalValue, (value) => {
  const found = supportedCalendarIntervals.find((item) => {
    if (item.value === value) {
      return true;
    }
    return false;
  })
  selectedCalendarInterval.value = found;
}, { immediate: true })
//#endregion

//#region Agg Data
const totalRuns = ref(0);
const totalPassedRuns = ref(0);
const totalNotPassedRuns = ref(0);
const totalPassedDataSet = ref<number[]>([]);
const totalFailedDataSet = ref<number[]>([]);
const dataLabels = ref<string[]>([]);

const timeFormat = 'YYYY-MM-DDTHH:mm:ss.SSSZ';

async function refreshDataSets() {
  console.log("enter refreshDataSets--General");

  const timeRange = {...props.timeRange};
  DateUtils.processTimeRange(timeRange);

  let reqBody = {
      guid: uuidv4(),
      startTime: timeRange.fromDayjs.format(timeFormat),
      endTime: timeRange.toDayjs.format(timeFormat),
      interval: selectedCalendarInterval.value.value,
      timeZone: dayjs().format('Z'),
      format: selectedCalendarInterval.value.format,
      projectName: props.selectedProject.name,
      environmentName: environmentName.value,
      templateName: templateName.value,
      runTag: runTag.value,
  };

  const rsp = await gElkAggApi.createDateHistogram(reqBody);

  if (rsp.error) {
    gNotificationService.error(rsp.error.message);
    return;
  }

  if (!rsp.data) {
    return;
  }

  const data = rsp.data;

  console.log("dateHistogramRsp=%s", StrUtils.pprint(data));

  totalRuns.value = data.total;
  totalPassedRuns.value = data.totalPassed;
  totalNotPassedRuns.value = data.totalNotPassed;

  let labels = [];
  let passedRunsDataSets = [];
  let notPassedRunsDataSets = [];

  for( let bucket of data.buckets) {
      labels.push(bucket.key);
      passedRunsDataSets.push(bucket.passedCount);
      notPassedRunsDataSets.push(bucket.notPassedCount);
  }

  dataLabels.value = labels;
  totalPassedDataSet.value = passedRunsDataSets;
  totalFailedDataSet.value = notPassedRunsDataSets;
}
//#endregion

//#region Chart
const barChartOptions = {
  responsive: true,
  scales: {
    x: {
      stacked: true,
    },
    y: {
      stacked: true,
      beginAtZero: true
    }
  },
  plugins: {
    tooltip: {
      mode: 'index',
      intersect: false,
    } as any,
  },
};

const totalRunChartData = computed(() => {
  return {
    type: 'bar' as const,
    data: {
      labels: dataLabels.value,
      datasets: [
        {
          label: 'Passed',
          data: totalPassedDataSet.value,
          borderWidth: 1,
          backgroundColor: 'green'
        },
        {
          label: 'NotPassed',
          data: totalFailedDataSet.value,
          borderWidth: 1,
          backgroundColor: 'red'
        }
      ]
    },
    options: barChartOptions,
  };
});

let totalRunsChart: any;

onBeforeMount(async () => {
  await refreshDataSets();

  const ctx1: any = document.getElementById("general-total-runs-chart");

  totalRunsChart = new Chart(ctx1, totalRunChartData.value);
});

async function updateTotalRunsChart() {
    console.log("enter updateTotalRunsChart--General");

    await refreshDataSets();

    totalRunsChart.data.labels = dataLabels.value;
    totalRunsChart.data.datasets[0].data = totalPassedDataSet.value;
    totalRunsChart.data.datasets[1].data = totalFailedDataSet.value;
    totalRunsChart.update();
}
//#endregion
</script>

<style lang="scss" scoped>
.container {
  width: 100%;
  height: 100%;

  .toolbar {
    margin-bottom: 16px;
    display: flex;
    flex-wrap: wrap;
    align-items: center;

    .select-environment {
      width: 160px;
    }

    .template-input {
      width: 200px;
    }

    .input {
      width: 160px;
    }
  }

  .summary-line {
    margin-bottom: 16px;
    display: flex;
    flex-wrap: wrap;
    align-items: center;

    font-size: 1.2rem;
  }

  .chart-container {
    width: 100%;
    height: calc(100% - 150px);
  }
}
</style>
