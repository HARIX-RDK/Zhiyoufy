<template>
  <div class="container">
    <div class="datetime-line">
      <div class="mr-4">From</div>

      <el-date-picker
        v-model="fromDatetime"
        type="datetime"
        placeholder="Select from datetime"
      />

      <div class="mx-4">To</div>

      <el-date-picker
        v-model="toDatetime"
        type="datetime"
        placeholder="Select to datetime"
      />
    </div>

    <el-button
      class="mt-4"
      :disabled="!rangeValid"
      type="primary"
      @click="onClickApply"
    >
      Apply
    </el-button>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onBeforeMount } from 'vue';

import dayjs from 'dayjs';

import { TimeRange } from '@/model/datetime-range';

const props = defineProps({
  inAbsolute: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(['out-time-range']);

onBeforeMount(() => {
  console.log('props.inAbsolute', props.inAbsolute);
  fromDatetime.value = props.inAbsolute.from.toDate();
  toDatetime.value = props.inAbsolute.to.toDate();
})

const fromDatetime = ref<Date>();
const toDatetime = ref<Date>();

const rangeValid = computed(() => {
  if(!fromDatetime.value || !toDatetime.value || fromDatetime.value.getTime() > toDatetime.value.getTime()) {
      return false;
  }

  return true;
});

function onClickApply() {
  const timeRange = new TimeRange();
  timeRange.from = dayjs(fromDatetime.value);
  timeRange.to = dayjs(toDatetime.value);
  timeRange.mode = 'absolute';

  emit('out-time-range', timeRange);
}
</script>

<style lang="scss" scoped>
.container {
  .datetime-line {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
  }

  .datetime-label {
    width: 50px;
  }
}
</style>
