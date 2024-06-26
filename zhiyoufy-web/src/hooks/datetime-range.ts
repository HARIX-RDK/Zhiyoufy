import { shallowRef } from 'vue';

import _ from 'lodash';

import { TimeRange } from '@/model/datetime-range';

import { DateUtils } from '@/utils';

export const useDatetimeRange = () => {
  const timeRange = new TimeRange();
  timeRange.mode = 'quick';
  timeRange.from = 'now-30d';
  timeRange.to = 'now';
  timeRange.display = 'Last 30 days';

  DateUtils.processTimeRange(timeRange);

  const currentTimeRange = shallowRef<TimeRange>(timeRange);
  const lastAbsolute = shallowRef();
  const lastRelative = shallowRef();

  function setTimeRange(newTimeRange: TimeRange) {
    const timeRange = _.clone(newTimeRange);

    DateUtils.processTimeRange(timeRange);

    currentTimeRange.value = timeRange;
  }

  function moveTimeBack() {
    const timeRange = _.clone(currentTimeRange.value);

    if (timeRange.mode === 'quick' || timeRange.mode === 'relative') {
      timeRange.from = timeRange.fromDayjs;
      timeRange.to = timeRange.toDayjs;
      timeRange.mode = 'absolute';
    }

    if (timeRange.unit) {
      timeRange.from = timeRange.from.subtract(1, timeRange.unit);
      timeRange.to = timeRange.to.subtract(1, timeRange.unit);
    } else {
      const diff = timeRange.to.diff(timeRange.from);
      timeRange.from = timeRange.from.subtract(diff + 1, 'ms');
      timeRange.to = timeRange.to.subtract(diff + 1, 'ms');
    }

    timeRange.display = timeRange.from.format(DateUtils.timeFormat) + ' - ' + timeRange.to.format(DateUtils.timeFormat);

    currentTimeRange.value = timeRange;
  }

  function moveTimeForward() {
    const timeRange = _.clone(currentTimeRange.value);

    if (timeRange.mode === 'quick' || timeRange.mode === 'relative') {
      timeRange.from = timeRange.fromDayjs;
      timeRange.to = timeRange.toDayjs;
      timeRange.mode = 'absolute';
    }

    if (timeRange.unit) {
      timeRange.from = timeRange.from.add(1, timeRange.unit);
      timeRange.to = timeRange.to.add(1, timeRange.unit);
    } else {
      const diff = timeRange.to.diff(timeRange.from);
      timeRange.from = timeRange.from.add(diff + 1, 'ms');
      timeRange.to = timeRange.to.add(diff + 1, 'ms');
    }

    timeRange.display = timeRange.from.format(DateUtils.timeFormat) + ' - ' + timeRange.to.format(DateUtils.timeFormat);

    currentTimeRange.value = timeRange;
  }

  return {
    currentTimeRange,
    lastAbsolute,
    lastRelative,

    setTimeRange,
    moveTimeBack,
    moveTimeForward,
  }
};
