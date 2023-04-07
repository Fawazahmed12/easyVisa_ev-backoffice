import { UscisEditionDatesRequestState } from './state';

import { uscisEditionDatesGetRequestReducer } from './uscis-edition-dates-get/state';
import { uscisEditionDatesPutRequestReducer } from './uscis-edition-dates-put/state';

export function reducer(state: UscisEditionDatesRequestState = {}, action): UscisEditionDatesRequestState {
  return {
    uscisEditionDatesGet: uscisEditionDatesGetRequestReducer(state.uscisEditionDatesGet, action),
    uscisEditionDatesPut: uscisEditionDatesPutRequestReducer(state.uscisEditionDatesPut, action),
  };
}
