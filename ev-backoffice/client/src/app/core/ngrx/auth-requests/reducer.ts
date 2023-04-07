import { loginRequestReducer } from './login/state';
import { logoutRequestReducer } from './logout/state';
import { changePasswordPutRequestReducer } from './change-password/state';

import { AuthRequestState } from './state';
import { loginInModalRequestReducer } from './login-in-modal/state';

export function reducer(state: AuthRequestState = {}, action): AuthRequestState {
  return {
    login: loginRequestReducer(state.login, action),
    loginInModal: loginInModalRequestReducer(state.loginInModal, action),
    logout: logoutRequestReducer(state.logout, action),
    changePasswordPut: changePasswordPutRequestReducer(state.changePasswordPut, action),
  };
}
