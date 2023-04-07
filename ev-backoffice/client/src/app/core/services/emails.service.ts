import { Injectable } from '@angular/core';

import { filter, share } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { select, Store } from '@ngrx/store';

import { State } from '../ngrx/state';
import { RequestState } from '../ngrx/utils';
import { previewUnsavedEmailPostRequestHandler } from '../ngrx/emails-requests/preview-unsaved-email-post/state';
import {
  emailByIdGetRequestHandler, emailPutRequestHandler,
  selectEmailByIdGetRequestState, selectEmailPutRequestState,
  selectPreviewUnsavedEmailPostRequestState
} from '../ngrx/emails-requests/state';
import { throwIfRequestFailError } from '../ngrx/utils/rxjs-utils';
import { Email } from '../models/email.model';
import { PreviewedEmail } from '../models/previewed-email.model';

@Injectable()
export class EmailsService {
  getPreviewUnsavedEmailPostRequest$: Observable<RequestState<PreviewedEmail>>;
  getEmailByIdGetRequest$: Observable<RequestState<Email>>;
  getEmailPutRequest$: Observable<RequestState<Email>>;

  constructor(
    private store: Store<State>,
  ) {
    this.getPreviewUnsavedEmailPostRequest$ = this.store.pipe(select(selectPreviewUnsavedEmailPostRequestState));
    this.getEmailByIdGetRequest$ = this.store.pipe(select(selectEmailByIdGetRequestState));
    this.getEmailPutRequest$ = this.store.pipe(select(selectEmailPutRequestState));
  }

  previewUnsavedEmail(data) {
    this.store.dispatch(previewUnsavedEmailPostRequestHandler.requestAction(data));
    return this.getPreviewUnsavedEmailPostRequest$.pipe(
      share(),
    );
  }

  getEmailById(emailId) {
    this.store.dispatch(emailByIdGetRequestHandler.requestAction(emailId));
    return this.getEmailByIdGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  updateEmail(email) {
    this.store.dispatch(emailPutRequestHandler.requestAction(email));
    return this.getEmailPutRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

}
