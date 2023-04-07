import { RepresentativesRequestState } from './state';
import { representativesGetRequestReducer } from './representatives-get/state';
import { attorneysValidatePostRequestReducer } from './attorneys-validate-post/state';
import { attorneyPatchRequestReducer } from './attorney-patch/state';
import { representativesMenuGetRequestReducer } from './representatives-menu-get/state';
import { feeScheduleRequestReducer } from './fee-schedule-get/state';


export function reducer(state: RepresentativesRequestState = {}, action): RepresentativesRequestState {
  return {
    representativesGet: representativesGetRequestReducer(state.representativesGet, action),
    representativesMenuGet: representativesMenuGetRequestReducer(state.representativesMenuGet, action),
    attorneysValidatePost: attorneysValidatePostRequestReducer(state.attorneysValidatePost, action),
    attorneyPatch: attorneyPatchRequestReducer(state.attorneyPatch, action),
    feeScheduleGet: feeScheduleRequestReducer(state.feeScheduleGet, action),
  };
}
