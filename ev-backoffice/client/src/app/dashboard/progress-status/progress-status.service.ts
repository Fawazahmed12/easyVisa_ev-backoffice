import { Injectable } from '@angular/core';

import { select, Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { filter, share } from 'rxjs/operators';

import { RequestState } from '../../core/ngrx/utils';
import { throwIfRequestFailError } from '../../core/ngrx/utils/rxjs-utils';

import { ProgressStatus } from './models/progress-status.model';
import { getQuestionnaireProgress, getQuestionnaireProgressGetRequestState, State, getDocumentProgress } from './ngrx/state';
import { GetDocumentProgress, GetQuestionnaireProgress } from './ngrx/progress-statuses/progress-statuses.actions';


@Injectable()
export class ProgressStatusService {
  questionnaireProgress$: Observable<ProgressStatus[]>;
  documentProgress$: Observable<ProgressStatus[]>;
  questionnaireProgressGetState$: Observable<RequestState<ProgressStatus[]>>;
  documentProgressGetState$: Observable<RequestState<ProgressStatus[]>>;

  constructor(
    private store: Store<State>
  ) {
    this.questionnaireProgress$ = this.store.pipe(select(getQuestionnaireProgress));
    this.documentProgress$ = this.store.pipe(select(getDocumentProgress));
    this.questionnaireProgressGetState$ = this.store.pipe(select(getQuestionnaireProgressGetRequestState));
    this.documentProgressGetState$ = this.store.pipe(select(getQuestionnaireProgressGetRequestState));
  }

  getQuestionnaireProgress(data) {
      this.store.dispatch(new GetQuestionnaireProgress(data));
      return this.questionnaireProgressGetState$.pipe(
        filter(response => !response.loading),
        throwIfRequestFailError(),
        share()
      );
  }

  getDocumentProgress(data) {
    this.store.dispatch(new GetDocumentProgress(data));
    return this.documentProgressGetState$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }
}
