import { AlertHandlingRequestState} from './state';
import { alertReplyPutRequestReducer } from './alert-reply-put/state';

export function reducer(state: AlertHandlingRequestState = {}, action): AlertHandlingRequestState {
  return {
    alertReplyPut: alertReplyPutRequestReducer(state.alertReplyPut, action),
  };
}
