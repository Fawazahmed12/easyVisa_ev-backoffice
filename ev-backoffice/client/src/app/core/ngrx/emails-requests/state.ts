import { createFeatureSelector, createSelector } from '@ngrx/store';

import { Email } from '../../models/email.model';
import { PreviewedEmail } from '../../models/previewed-email.model';

import { RequestState } from '../utils';

export const EMAILS_REQUESTS = 'EmailsRequests';

export interface EmailsRequestState {
  emailByIdGet?: RequestState<Email>;
  emailPost?: RequestState<Email>;
  emailPut?: RequestState<Email>;
  previewUnsavedEmailPost?: RequestState<PreviewedEmail>;
}

export const selectEmailsRequestState = createFeatureSelector<EmailsRequestState>(EMAILS_REQUESTS);

export const selectEmailPostRequestState = createSelector(
  selectEmailsRequestState,
  (state: EmailsRequestState) => state.emailPost,
);

export const selectEmailPutRequestState = createSelector(
  selectEmailsRequestState,
  (state: EmailsRequestState) => state.emailPut,
);

export const selectPreviewUnsavedEmailPostRequestState = createSelector(
  selectEmailsRequestState,
  (state: EmailsRequestState) => state.previewUnsavedEmailPost,
);

export const selectEmailByIdGetRequestState = createSelector(
  selectEmailsRequestState,
  (state: EmailsRequestState) => state.emailByIdGet,
);

export { emailByIdGetRequestHandler } from './email-by-id-get/state';
export { emailPostRequestHandler } from './email-post/state';
export { emailPutRequestHandler } from './email-put/state';
export { previewUnsavedEmailPostRequestHandler } from './preview-unsaved-email-post/state';

