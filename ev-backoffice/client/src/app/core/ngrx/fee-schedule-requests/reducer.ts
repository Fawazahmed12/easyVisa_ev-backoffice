import { FeeScheduleRequestState } from './state';

import { feeScheduleSettingsGetRequestReducer } from './fee-schedule-settings-get/state';
import { feeScheduleSettingsPostRequestReducer } from './fee-schedule-settings-post/state';

export function reducer(state: FeeScheduleRequestState = {}, action): FeeScheduleRequestState {
  return {
    feeScheduleSettingsGet: feeScheduleSettingsGetRequestReducer(state.feeScheduleSettingsGet, action),
    feeScheduleSettingsPost: feeScheduleSettingsPostRequestReducer(state.feeScheduleSettingsPost, action),
  };
}
