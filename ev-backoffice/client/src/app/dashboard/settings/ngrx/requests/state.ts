import { RequestState } from '../../../../core/ngrx/utils';
import { GovernmentFee } from '../../../../core/models/government-fee.model';
import { RankingData } from '../../../models/ranking-data.model';
import { RepresentativesCount } from '../../../models/representatives-count.model';
import { Job } from '../../../models/site-jobs';


export const DASHBOARD_SETTINGS_MODULE_REQUESTS = 'DashboardSettingsModuleRequests';

export interface DashboardSettingsModuleRequestState {
  governmentFeePost?: RequestState<GovernmentFee>;
  rankingDataGet?: RequestState<RankingData>;
  rankingDataPut?: RequestState<RankingData>;
  representativesCountGet?: RequestState<RepresentativesCount>;
  batchJobsGet?: RequestState<Job>;
  batchJobsPatch?: RequestState<Job>;
}

export const selectDashboardSettingsModuleRequestsState = (state) => state[DASHBOARD_SETTINGS_MODULE_REQUESTS];

export const selectGovernmentFeePostRequestState = (state: DashboardSettingsModuleRequestState) => state.governmentFeePost;
export const selectRankingDataGetRequestState = (state: DashboardSettingsModuleRequestState) => state.rankingDataGet;
export const selectRankingDataPutRequestState = (state: DashboardSettingsModuleRequestState) => state.rankingDataPut;
export const selectRepresentativesGetRequestState = (state: DashboardSettingsModuleRequestState) => state.representativesCountGet;
export const selectBatchJobsGetRequestState = (state: DashboardSettingsModuleRequestState) => state.batchJobsGet;
export const selectBatchJobsPatchRequestState = (state: DashboardSettingsModuleRequestState) => state.batchJobsPatch;

export { governmentFeePostRequestHandler } from './government-fee-post/state';
export { rankingDataGetRequestHandler } from './ranking-data-get/state';
export { rankingDataPutRequestHandler } from './ranking-data-put/state';
export { representativesCountGetRequestHandler } from './representatives-count-get/state';
export { batchJobsGetRequestHandler } from './batch-jobs-get/state';
export { batchJobsPatchRequestHandler } from './batch-jobs-patch/state';
