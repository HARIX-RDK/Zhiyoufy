import dayjs, { Dayjs } from 'dayjs';

import type { TimeRange } from '@/model/datetime-range';

export class DateUtils {
  static units = ['y', 'Q', 'M', 'w', 'd', 'h', 'm', 's', 'ms'];
  static unitsDesc = DateUtils.units;
  static unitsAsc = [...DateUtils.unitsDesc].reverse();
  static timeFormat = 'YYYY-MM-DD HH:mm:ss.SSS';

  static isDate(d: any) {
    return Object.prototype.toString.call(d) === '[object Date]';
  }

  static isValidDate(d: any) {
    return DateUtils.isDate(d) && !isNaN(d.valueOf());
  }

  /*
  * This is a simplified version of elasticsearch's date parser.
  * If you pass in a momentjs instance as the third parameter the calculation
  * will be done using this (and its locale settings) instead of the one bundled
  * with this library.
  */
  static parse(text: any, { roundUp = false } = {}): Dayjs | undefined {
    if (!text) { return undefined; }
    if (dayjs.isDayjs(text)) { return text; }
    if (DateUtils.isDate(text)) { return dayjs(text); }

    let time;
    let mathString = '';
    let index;
    let parseString;

    if (text.substring(0, 3) === 'now') {
      time = dayjs();
      mathString = text.substring('now'.length);
    } else {
      index = text.indexOf('||');
      if (index === -1) {
        parseString = text;
        mathString = ''; // nothing else
      } else {
        parseString = text.substring(0, index);
        mathString = text.substring(index + 2);
      }
      // We're going to just require ISO8601 timestamps, k?
      time = dayjs(parseString);
    }

    if (!mathString.length) {
      return time;
    }

    return DateUtils.parseDateMath(mathString, time, roundUp);
  }

  static parseDateMath(mathString: string, time: Dayjs, roundUp: boolean): Dayjs | undefined {
    let dateTime = time;
    const len = mathString.length;
    let i = 0;

    while (i < len) {
      const c = mathString.charAt(i++);
      let type: number;
      let num: number;
      let unit: any;

      if (c === '/') {
        type = 0;
      } else if (c === '+') {
        type = 1;
      } else if (c === '-') {
        type = 2;
      } else {
        return;
      }

      if (isNaN(parseInt(mathString.charAt(i), 10))) {
        num = 1;
      } else if (mathString.length === 2) {
        num = parseInt(mathString.charAt(i), 10);
      } else {
        const numFrom = i;
        while (!isNaN(parseInt(mathString.charAt(i), 10))) {
          i++;
          if (i > 10) { return; }
        }
        num = parseInt(mathString.substring(numFrom, i), 10);
      }

      if (type === 0) {
        // rounding is only allowed on whole, single, units (eg M or 1M, not 0.5M or 2M)
        if (num !== 1) {
          return;
        }
      }

      unit = mathString.charAt(i++);

      // append additional characters in the unit
      for (let j = i; j < len; j++) {
        const unitChar = mathString.charAt(i);
        if (/[a-z]/i.test(unitChar)) {
          unit += unitChar;
          i++;
        } else {
          break;
        }
      }

      if (DateUtils.units.indexOf(unit) === -1) {
        return;
      } else {
        if (unit === 'w') {
          unit = 'isoWeek';
        } else if (unit === 'Q') {
          unit = 'quarter';
        }
        if (type === 0) {
          if (roundUp) {
            dateTime = dateTime.endOf(unit);
          } else {
            dateTime = dateTime.startOf(unit);
          }
        } else if (type === 1) {
          dateTime = dateTime.add(num, unit);
        } else if (type === 2) {
          dateTime = dateTime.subtract(num, unit);
        }
      }
    }

    return dateTime;
  }

  static processTimeRange(timeRange: TimeRange) {
    let from;
    let to;

    if (timeRange.mode === 'quick') {
      from = DateUtils.parse(timeRange.from);
      to = DateUtils.parse(timeRange.to, {roundUp: true});
    } else if (timeRange.mode === 'relative') {
      const fromStr = DateUtils.getRelativeString(timeRange, 'from');
      const toStr = DateUtils.getRelativeString(timeRange, 'to');
      from = DateUtils.parse(fromStr);
      to = DateUtils.parse(toStr, {roundUp: true});

      timeRange.display = `${fromStr} to ${toStr}`;
    } else if (timeRange.mode === 'absolute') {
      from = timeRange.from;
      to = timeRange.to;

      timeRange.display = timeRange.from.format(DateUtils.timeFormat) + ' - ' + timeRange.to.format(DateUtils.timeFormat);
    }

    timeRange.fromDayjs = from;
    timeRange.toDayjs = to;
  }

  static getRelativeString(timeRange: TimeRange, key: 'from' | 'to') {
    const relative = timeRange;

    let count = 0;
    let round = false;
    let matches = 's';

    if (key === 'from') {
      count = relative.from.count;
      round = relative.from.round;
      matches = relative.from.unit.match(/([smhdwMy])(\+)?/);
    } else {
      count = relative.to.count;
      round = relative.to.round;
      matches = relative.to.unit.match(/([smhdwMy])(\+)?/);
    }

    let unit;
    let operator = '-';
    if (matches && matches[1]) { unit = matches[1]; }
    if (matches && matches[2]) { operator = matches[2]; }
    if (count === 0 && !round) { return 'now'; }
    let result = `now${operator}${count}${unit}`;
    result += (round ? '/' + unit : '');

    return result;
  }
}
