import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';

import { EstimatedTax } from '../../models/estimated-tax.model';

import { RequestFailAction, RequestSuccessAction } from '../utils';
import { estimatedTaxPostRequestHandler } from '../taxes-requests/post-estimated-tax/state';

import { PostEstimatedTax, PostEstimatedTaxFailure, PostEstimatedTaxSuccess, TaxesActionTypes } from './taxes.actions';
import { ModalService } from '../../services';


@Injectable()
export class TaxesEffects {

  @Effect()
  PostEstimatedTax$: Observable<Action> = this.actions$.pipe(
    ofType(TaxesActionTypes.PostEstimatedTax),
    map(({payload}: PostEstimatedTax) => estimatedTaxPostRequestHandler.requestAction(payload))
  );

  @Effect()
  PostEstimatedTaxSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(estimatedTaxPostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<EstimatedTax>) => new PostEstimatedTaxSuccess(payload))
  );

  @Effect()
  PostEstimatedTaxFail$: Observable<Action> = this.actions$.pipe(
    ofType(estimatedTaxPostRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new PostEstimatedTaxFailure(payload))
  );

  @Effect({dispatch: false})
  showErrorModal$: Observable<Action> = this.actions$.pipe(
    ofType(TaxesActionTypes.PostEstimatedTaxFailure),
    switchMap(({payload}: RequestFailAction<any>) => this.modalService.showErrorModal(payload.error.errors || payload.error))
  );


  constructor(
    private actions$: Actions,
    private modalService: ModalService,
  ) {
  }
}
