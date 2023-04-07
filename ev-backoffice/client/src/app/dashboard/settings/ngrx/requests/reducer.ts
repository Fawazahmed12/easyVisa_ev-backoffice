import { DashboardSettingsModuleRequestState } from './state';
import { governmentFeePostRequestReducer } from './government-fee-post/state';
import { rankingDataGetRequestReducer } from './ranking-data-get/state';
import { rankingDataPutRequestReducer } from './ranking-data-put/state';
import { representativesCountGetRequestReducer } from './representatives-count-get/state';
import { batchJobsGetRequestReducer } from './batch-jobs-get/state';
import { batchJobsPatchRequestReducer } from './batch-jobs-patch/state';

export function reducer(state: DashboardSettingsModuleRequestState = {}, action): DashboardSettingsModuleRequestState {
  return {
    governmentFeePost: governmentFeePostRequestReducer(state.governmentFeePost, action),
    rankingDataGet: rankingDataGetRequestReducer(state.rankingDataGet, action),
    rankingDataPut: rankingDataPutRequestReducer(state.rankingDataPut, action),
    representativesCountGet: representativesCountGetRequestReducer(state.representativesCountGet, action),
    batchJobsGet: batchJobsGetRequestReducer(state.batchJobsGet, action),
    batchJobsPatch: batchJobsPatchRequestReducer(state.batchJobsPatch, action),
  };
}
