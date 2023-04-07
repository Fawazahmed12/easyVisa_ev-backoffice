import { userGetRequestReducer } from './get/state';

import { UserRequestState } from './state';
import { userIdByEVIdGetRequestReducer } from './get-user-id-by-evid/state';
import { userDeleteRequestReducer } from './delete-user/state';
import { changeMembershipPatchRequestReducer } from './change-membership/state';
import { convertToAttorneyPostRequestReducer } from './convert-to-attorney/state';

export function reducer(state: UserRequestState = {}, action) {
  return {
    get: userGetRequestReducer(state.get, action),
    userDelete: userDeleteRequestReducer(state.userDelete, action),
    userIdByEVIdGet: userIdByEVIdGetRequestReducer(state.userIdByEVIdGet, action),
    changeMembershipPatch: changeMembershipPatchRequestReducer(state.changeMembershipPatch, action),
    convertToAttorneyPost: convertToAttorneyPostRequestReducer(state.convertToAttorneyPost, action),
  };
}
