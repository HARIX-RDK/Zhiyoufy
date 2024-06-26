<template>
  <div class="container">
    <el-tabs v-model="activeTabName">
      <el-tab-pane label="Quick" name="Quick">
        <div class="tab">
          <DatetimeRangePickerQuick
            @out-time-range="onApply"
          >
          </DatetimeRangePickerQuick>
        </div>
      </el-tab-pane>

      <el-tab-pane label="Absolute" name="Absolute">
        <div class="tab">
          <DatetimeRangePickerAbsolute
            :in-absolute="props.datetimeRangeHook.lastAbsolute.value"
            @out-time-range="onApply"
          ></DatetimeRangePickerAbsolute>
        </div>
      </el-tab-pane>

      <el-tab-pane label="Relative" name="Relative">
        <div class="tab">
          <DatetimeRangePickerRelative
            :in-relative="props.datetimeRangeHook.lastRelative.value"
            @out-time-range="onApply"
          >
          </DatetimeRangePickerRelative>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onBeforeMount } from 'vue';

import DatetimeRangePickerAbsolute from './DatetimeRangePickerAbsolute.vue';
import DatetimeRangePickerQuick from './DatetimeRangePickerQuick.vue'
import DatetimeRangePickerRelative from './DatetimeRangePickerRelative.vue';

import dayjs from 'dayjs';

const props = defineProps({
  datetimeRangeHook: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(['ok']);

const activeTabName = ref('Quick');

onBeforeMount(() => {
  const { lastAbsolute, lastRelative } = props.datetimeRangeHook;

  if (!lastAbsolute.value) {
    lastAbsolute.value = {
      from: dayjs(),
      to: dayjs(),
      mode: "absolute",
    }
  }

  if (!lastRelative.value) {
    lastRelative.value = {
      from: {
        count: 30,
        unit: 'm',
        round: false
      },
      to: {
        count: 0,
        unit: 'm',
        round: false
      },
      mode: "relative",
    }
  }
});

function onApply(timeRange: any) {
  console.log("onApply entered, timeRange=%s", JSON.stringify(timeRange));

  const { lastAbsolute, lastRelative, setTimeRange } = props.datetimeRangeHook;

  if (timeRange.mode === "absolute") {
    lastAbsolute.value = timeRange;
  } else if (timeRange.mode === "relative") {
    lastRelative.value = timeRange;
  }

  setTimeRange(timeRange);

  emit('ok');
}
</script>

<style scoped>
.container {
}
</style>
