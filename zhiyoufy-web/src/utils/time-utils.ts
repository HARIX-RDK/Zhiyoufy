import dayjs from 'dayjs';

export class TimeUtils {
  static sleep(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  static displayTimeUptoSecs(datetime?: string) {
    if (!datetime) {
      return '';
    }

    const dt = dayjs(datetime);

    if (!dt.isValid()) {
      return '';
    }

    return dt.format('YYYY-MM-DD HH:mm:ss');
  }

  static computeDurationSecs(datetime?: string, datetime2?: string) {
    if (!datetime) {
      return '';
    }

    const dt = dayjs(datetime);

    if (!dt.isValid()) {
      return '';
    }

    let dt2;

    if (!datetime2) {
      dt2 = dayjs();
    } else {
      dt2 = dayjs(datetime2);
    }

    if (!dt2.isValid()) {
      return '';
    }

    const durationSecs = dt2.diff(dt, 'second');

    return `${durationSecs}s`
  }
}
