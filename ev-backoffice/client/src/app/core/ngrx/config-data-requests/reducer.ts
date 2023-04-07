import { ConfigDataRequestState } from './state';

import { feeDetailsGetRequestReducer } from './fee-details-get/state';
import { governmentFeeGetRequestReducer } from './government-fee-get/state';
import { benefitsGetRequestReducer } from './benefits-get/state';
import { feeDetailsPostRequestReducer } from './fee-details-post/state';

export function reducer(state: ConfigDataRequestState = {}, action): ConfigDataRequestState {
  return {
    feeDetailsGet: feeDetailsGetRequestReducer(state.feeDetailsGet, action),
    feeDetailsPost: feeDetailsPostRequestReducer(state.feeDetailsPost, action),
    governmentFeeGet: governmentFeeGetRequestReducer(state.governmentFeeGet, action),
    benefitsGet: benefitsGetRequestReducer(state.benefitsGet, action),
  };
}
