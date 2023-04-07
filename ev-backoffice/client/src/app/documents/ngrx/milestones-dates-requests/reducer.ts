import { MilestoneDatesRequestState } from './state';

import { milestoneDatesGetRequestReducer } from './milestone-dates-get/state';
import { milestoneDatePostRequestReducer } from './milestone-date-post/state';


export function reducer(state: MilestoneDatesRequestState = {}, action): MilestoneDatesRequestState {
  return {
    milestoneDatesGet: milestoneDatesGetRequestReducer(state.milestoneDatesGet, action),
    milestoneDatePost: milestoneDatePostRequestReducer(state.milestoneDatePost, action),
  };
}
