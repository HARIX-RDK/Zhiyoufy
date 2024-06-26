<template>
  <div class="container">
    <div v-for="(list, index) in quickLists" :key="index">
      <div class="quick-list-row-container">
        <template v-for="option in list" :key="option.display">
          <el-button
              class="quick-option-button"
              @click="applyQuick(option)">
              {{ option.display }}
          </el-button>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const emit = defineEmits(['out-time-range']);

const quickLists: any[][] = [
  [
    { from: 'now/d',    to: 'now/d',    display: 'Today',                 section: 0, unit: 'd' },
    { from: 'now/w',    to: 'now/w',    display: 'This week',             section: 0, unit: 'w' },
    { from: 'now/M',    to: 'now/M',    display: 'This month',            section: 0, unit: 'M'},
    { from: 'now/Q',    to: 'now/Q',    display: 'This quarter',          section: 0, unit: 'Q'},
    { from: 'now/y',    to: 'now/y',    display: 'This year',             section: 0, unit: 'y'},
    { from: 'now/d',    to: 'now',      display: 'Today so far',          section: 0, unit: 'd'},
    { from: 'now/w',    to: 'now',      display: 'Week to date',          section: 0, unit: 'w'},
    { from: 'now/M',    to: 'now',      display: 'Month to date',         section: 0, unit: 'M'},
    { from: 'now/Q',    to: 'now',      display: 'Quarter to date',       section: 0, unit: 'Q'},
    { from: 'now/y',    to: 'now',      display: 'Year to date',          section: 0, unit: 'y' }
  ],
  [
    { from: 'now-15m',  to: 'now',      display: 'Last 15 minutes',       section: 1 },
    { from: 'now-30m',  to: 'now',      display: 'Last 30 minutes',       section: 1 },
    { from: 'now-1h',   to: 'now',      display: 'Last 1 hour',           section: 1 },
    { from: 'now-4h',   to: 'now',      display: 'Last 4 hours',          section: 1 },
    { from: 'now-12h',  to: 'now',      display: 'Last 12 hours',         section: 1 },
    { from: 'now-24h',  to: 'now',      display: 'Last 24 hours',         section: 1 },
    { from: 'now-7d',   to: 'now',      display: 'Last 7 days',           section: 1 }
  ],
  [
    { from: 'now-30d',  to: 'now',      display: 'Last 30 days',          section: 2 },
    { from: 'now-60d',  to: 'now',      display: 'Last 60 days',          section: 2 },
    { from: 'now-90d',  to: 'now',      display: 'Last 90 days',          section: 2 },
    { from: 'now-6M',   to: 'now',      display: 'Last 6 months',         section: 2 },
    { from: 'now-1y',   to: 'now',      display: 'Last 1 year',           section: 2 },
    { from: 'now-2y',   to: 'now',      display: 'Last 2 years',          section: 2 },
    { from: 'now-5y',   to: 'now',      display: 'Last 5 years',          section: 2 }
  ]
];

function applyQuick(option: any) {
    option.mode="quick";
    console.log("applyQuick entered, option=", JSON.stringify(option));
    emit('out-time-range', option);

}
</script>

<style lang="scss" scoped>
.container {
  .quick-list-row-container {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;

    margin-bottom: 20px;
  }
  .quick-option-button {
      width: 200px;
      margin-bottom: 10px;
      margin-left: 20px
  }
}
</style>
