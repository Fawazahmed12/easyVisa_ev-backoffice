import { Injectable } from '@angular/core';

import { filter, share } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { Action, select, Store } from '@ngrx/store';
import { Dictionary } from '@ngrx/entity';

import { throwIfRequestFailError } from '../../core/ngrx/utils/rxjs-utils';
import { State } from '../../core/ngrx/state';
import { RequestState } from '../../core/ngrx/utils';
import { GetEmailTemplate } from '../../core/ngrx/email-templates/email-templates.actions';
import { EmailTemplateTypes } from '../../core/models/email-template-types.enum';

import {
  getActiveDisposition,
  getActiveDispositionData,
  getActiveDispositionId,
  getDispositionDataGetRequestState,
  getDispositionPutRequestState,
  getDispositions,
  getDispositionsEntities,
  getDispositionsGetRequestState,
  getTotalDispositions,
} from '../ngrx/state';
import { Disposition } from '../models/dispositions.model';
import {
  ApproveActiveDisposition,
  DownloadActiveDispositionData,
  GetDispositionData,
  GetDispositions,
  PutDisposition,
  RejectDisposition,
  ResetActiveDisposition,
  SetActiveDisposition,
  SetNextActiveDispositionId,
  SetPreviousActiveDispositionId
} from '../ngrx/dispositions/dispositions.actions';
import { DispositionData } from '../models/disposition-data.model';
import { DispositionsEffects } from '../ngrx/dispositions/dispositions.effects';


@Injectable()
export class DispositionsService {
  getDispositionsGetRequest$: Observable<RequestState<Disposition[]>>;
  getDispositionDataGetRequest$: Observable<RequestState<DispositionData>>;
  getDispositionPutRequest$: Observable<RequestState<Disposition>>;
  dispositions$: Observable<Disposition[]>;
  totalDispositions$: Observable<string>;
  activeDisposition$: Observable<Disposition>;
  activeDispositionData$: Observable<DispositionData>;
  activeDispositionId$: Observable<number>;
  dispositionsEntities$: Observable<Dictionary<Disposition>>;
  putDispositionFailAction$: Observable<Action>;

  constructor(
    private store: Store<State>,
    private dispositionsEffects: DispositionsEffects,
  ) {
    this.dispositions$ = this.store.pipe(select(getDispositions));
    this.totalDispositions$ = this.store.pipe(select(getTotalDispositions));
    this.activeDisposition$ = this.store.pipe(select(getActiveDisposition));
    this.activeDispositionData$ = this.store.pipe(select(getActiveDispositionData));
    this.activeDispositionId$ = this.store.pipe(select(getActiveDispositionId));
    this.dispositionsEntities$ = this.store.pipe(select(getDispositionsEntities));
    this.getDispositionsGetRequest$ = this.store.pipe(select(getDispositionsGetRequestState));
    this.getDispositionDataGetRequest$ = this.store.pipe(select(getDispositionDataGetRequestState));
    this.getDispositionPutRequest$ = this.store.pipe(select(getDispositionPutRequestState));
    this.putDispositionFailAction$ = this.dispositionsEffects.putDispositionFail$;
  }

  getDispositions(params) {
    this.store.dispatch(new GetDispositions(params));
    return this.getDispositionsGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  getDispositionData(data) {
    this.store.dispatch(new GetDispositionData(data));
    return this.getDispositionDataGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  updateDisposition(data) {
    this.store.dispatch(new PutDisposition(data));
    return this.getDispositionPutRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  setActiveDisposition(id) {
    this.store.dispatch(new SetActiveDisposition(id));
  }

  resetActiveDisposition() {
    this.store.dispatch(new ResetActiveDisposition());
  }

  downloadData() {
    this.store.dispatch(new DownloadActiveDispositionData());
  }

  setNextId() {
    this.store.dispatch(new SetNextActiveDispositionId());
  }

  setPreviousId() {
    this.store.dispatch(new SetPreviousActiveDispositionId());
  }

  approveDisposition(data: {
    approve: boolean;
    organizationId: number;
    representativeId: number;
  }): void {
    this.store.dispatch(new ApproveActiveDisposition(data));
  }

  rejectDisposition(data: {
    approve: boolean;
    rejectionMailMessage: string;
    rejectionMailSubject: string;
    organizationId: number;
    representativeId: number;
  }): void {
    this.store.dispatch(new RejectDisposition(data));
  }

  getEmailTemplateForPopUp(representativeId) {
    this.store.dispatch(
      new GetEmailTemplate(
        {
          type: EmailTemplateTypes.DOCUMENT_REJECTION_NOTIFICATION,
          representativeId
        }
      ));
  }
}
