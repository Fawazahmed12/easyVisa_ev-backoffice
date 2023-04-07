import { EmailsRequestState } from './state';
import { emailPostRequestReducer } from './email-post/state';
import { emailPutRequestReducer } from './email-put/state';
import { previewUnsavedEmailPostRequestReducer } from './preview-unsaved-email-post/state';
import { emailByIdGetRequestReducer } from './email-by-id-get/state';

export function reducer(state: EmailsRequestState = {}, action): EmailsRequestState {
  return {
    emailPost: emailPostRequestReducer(state.emailPost, action),
    emailPut: emailPutRequestReducer(state.emailPut, action),
    emailByIdGet: emailByIdGetRequestReducer(state.emailByIdGet, action),
    previewUnsavedEmailPost: previewUnsavedEmailPostRequestReducer(state.previewUnsavedEmailPost, action),
  };
}
