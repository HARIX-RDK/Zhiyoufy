import { JobRunApiImpl } from './job-run-api-impl';
import { JobScheduleApiImpl } from './job-schedule-api-impl';

export const gJobRunApi = new JobRunApiImpl();
export const gJobScheduleApi = new JobScheduleApiImpl();
