import { GovernmentFeePostRequestEffects } from './government-fee-post/state';
import { RankingDataGetRequestEffects } from './ranking-data-get/state';
import { RankingDataPutRequestEffects } from './ranking-data-put/state';
import { RepresentativesCountGetRequestEffects } from './representatives-count-get/state';
import { BatchJobsGetRequestEffects } from './batch-jobs-get/state';
import { BatchJobsPatchRequestEffects } from './batch-jobs-patch/state';

export const DashboardSettingsModuleRequestEffects = [
  GovernmentFeePostRequestEffects,
  RankingDataGetRequestEffects,
  RankingDataPutRequestEffects,
  RepresentativesCountGetRequestEffects,
  BatchJobsGetRequestEffects,
  BatchJobsPatchRequestEffects,
];
