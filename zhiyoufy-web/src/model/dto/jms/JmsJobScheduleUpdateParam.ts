export interface JmsJobScheduleUpdateParam {
  runTag?: string;
  runNum?: number;
  parallelNum?: number;
  includeTags?: string;
  excludeTags?: string;
  addTags?: string;
  removeTags?: string;
  crontabConfig?: string;
}
