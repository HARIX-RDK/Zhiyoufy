export interface DatetimeRangeQuick {
  from: string;
  to: string;
  display: string;
  section: number;

  unit?: string;
  mode?: string;
}

export const QUICK_RANGES: DatetimeRangeQuick[][] = [
  [
    { from: 'now/d',    to: 'now/d',    display: 'Today',                  section: 0, unit: 'd' },
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
