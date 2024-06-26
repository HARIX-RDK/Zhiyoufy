export interface DatetimeRelative {
  count: number;
  unit: string;

  round?: boolean;
}

export interface DatetimeRangeRelative {
  from: DatetimeRelative;
  to: DatetimeRelative;
  mode: string;

  display?: string;
}

export const relativeOptions = [
  { text: 'Seconds ago', value: 's' },
  { text: 'Minutes ago', value: 'm' },
  { text: 'Hours ago', value: 'h' },
  { text: 'Days ago', value: 'd' },
  { text: 'Weeks ago', value: 'w' },
  { text: 'Months ago', value: 'M' },
  { text: 'Years ago', value: 'y' },
];
