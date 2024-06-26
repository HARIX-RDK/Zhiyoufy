<template>
  <div class="container">
    <div class="input-row">
      <div class="row-label">From</div>

      <el-input-number :min="0"
        class="ml-2"
        v-model="fromCountValue">
      </el-input-number>

      <el-select
        class="ml-2"
        v-model="fromUnitValue"
      >
        <el-option
          v-for="item in relativeOptions"
          :key="item.value"
          :label="item.text"
          :value="item.value"
        />
      </el-select>

      <el-checkbox v-model="fromRoundEnabled" label="Round" class="ml-2" />
    </div>

    <div class="input-row">
      <div class="row-label">To</div>

      <el-input-number :min="0"
        class="ml-2"
        v-model="toCountValue">
      </el-input-number>

      <el-select
        class="ml-2"
        v-model="toUnitValue"
      >
        <el-option
          v-for="item in relativeOptions"
          :key="item.value"
          :label="item.text"
          :value="item.value"
        />
      </el-select>

      <el-checkbox v-model="toRoundEnabled" label="Round" class="ml-2" />
    </div>

    <div class="input-row">
      {{ outRelative.display }}
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

import type { TimeRange } from '@/model/datetime-range';

import { relativeOptions } from '@/model/datetime-range';
import { DateUtils } from '@/utils';

const props = defineProps({
  inRelative: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(['out-time-range']);

onBeforeMount(() => {
  console.log('props.inRelative', props.inRelative);
  fromCountValue.value = props.inRelative.from.count;
  fromUnitValue.value = props.inRelative.from.unit;
  fromRoundEnabled.value = props.inRelative.from.round;

  toCountValue.value = props.inRelative.to.count;
  toUnitValue.value = props.inRelative.to.unit;
  toRoundEnabled.value = props.inRelative.to.round;
})

const fromCountValue = ref(30);
const fromUnitValue = ref('m');
const fromRoundEnabled = ref(false);

const toCountValue = ref(0);
const toUnitValue = ref('m');
const toRoundEnabled = ref(false);

const outRelative = computed(() => {
  const timeRange = {
        "from": {
            "count": fromCountValue.value,
            "unit": fromUnitValue.value,
            "round": fromRoundEnabled,
        },
        "to": {
            "count": toCountValue.value,
            "unit": toUnitValue.value,
            "round": toRoundEnabled.value,
        },
        "mode": "relative",
    } as TimeRange;

    DateUtils.processTimeRange(timeRange);

    console.log('timeRange 11', timeRange);

    return timeRange;
});

const rangeValid = computed(() => {
  return outRelative.value.fromDayjs.isBefore(outRelative.value.toDayjs);
});

function onClickApply() {
  emit('out-time-range', outRelative.value);
}
</script>

<style lang="scss" scoped>
.container {
  .input-row {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
  }

  .row-label {
    width: 50px;
  }
}
</style>
