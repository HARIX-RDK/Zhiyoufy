<template>
  <div class="component-container">
    <el-button :icon="ArrowLeft" @click="datetimeRangeHook.moveTimeBack">
    </el-button>

    <el-button :icon="Calendar" @click="datetimeRangePickerDialogVisible = true">
      {{ timeRangeDisplay }}
    </el-button>

    <el-button :icon="ArrowRight" @click="datetimeRangeHook.moveTimeForward">
    </el-button>

    <el-dialog
      v-if="datetimeRangePickerDialogVisible"
      v-model="datetimeRangePickerDialogVisible"
      :title="datetimeRangePickerDialogTitle"
      width="800px"
    >
      <DatetimeRangePickerDialog
        :datetime-range-hook="datetimeRangeHook"
        @ok="datetimeRangePickerDialogVisible = false"
      >
      </DatetimeRangePickerDialog>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { ArrowLeft, ArrowRight, Calendar } from '@element-plus/icons-vue';

import DatetimeRangePickerDialog from './DatetimeRangePickerDialog.vue';

import { useDatetimeRange } from '@/hooks';

const emit = defineEmits(['datetime-range-changed']);

const datetimeRangeHook = useDatetimeRange();

watch(datetimeRangeHook.currentTimeRange, () => {
  emit('datetime-range-changed', datetimeRangeHook.currentTimeRange.value);
}, { immediate: true })

const timeRangeDisplay = computed(() => {
  if (!datetimeRangeHook.currentTimeRange.value || !datetimeRangeHook.currentTimeRange.value.display) {
    return 'Invalid Time Range';
  }
  return datetimeRangeHook.currentTimeRange.value.display;
});

const datetimeRangePickerDialogVisible = ref(false);
const datetimeRangePickerDialogTitle = ref('Datetime Range Picker');
</script>

<style scoped>
.component-container {
  display: flex;
  align-items: center;
}
</style>
