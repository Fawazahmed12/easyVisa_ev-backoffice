import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';

import { UscisEditionDatesModel } from '../../models/uscis-edition-dates.model';
import { RequestFailAction, RequestSuccessAction } from '../utils';
import {
  GetUscisEditionDates,
  GetUscisEditionDatesSuccess,
  GetUscisEditionDatesFailure, UscisEditionDatesActionTypes, PutUscisEditionDatesSuccess, PutUscisEditionDates
} from './uscis-edition-dates.actions';
import { uscisEditionDatesGetRequestHandler } from '../uscis-edition-dates-requests/uscis-edition-dates-get/state';
import { uscisEditionDatesPutRequestHandler } from '../uscis-edition-dates-requests/uscis-edition-dates-put/state';

@Injectable()
export class UscisEditionDatesEffects {

  @Effect()
  getUscisEditionDates$: Observable<Action> = this.actions$.pipe(
    ofType(UscisEditionDatesActionTypes.GetUscisEditionDates),
    map(({payload}: GetUscisEditionDates) => uscisEditionDatesGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getUscisEditionDatesSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(uscisEditionDatesGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<UscisEditionDatesModel[]>) => new GetUscisEditionDatesSuccess(payload))
  );

  @Effect({dispatch: false})
  getUscisEditionDatesFail$: Observable<Action> = this.actions$.pipe(
    ofType(uscisEditionDatesGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  putUscisEditionDates$: Observable<Action> = this.actions$.pipe(
    ofType(UscisEditionDatesActionTypes.PutUscisEditionDates),
    map(({payload}: PutUscisEditionDates) => uscisEditionDatesPutRequestHandler.requestAction(payload))
  );

  @Effect()
  putUscisEditionDatesSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(uscisEditionDatesPutRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<UscisEditionDatesModel[]>) => new PutUscisEditionDatesSuccess(payload))
  );

  @Effect({dispatch: false})
  putUscisEditionDatesFail$: Observable<Action> = this.actions$.pipe(
    ofType(uscisEditionDatesPutRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );


  constructor(
    private actions$: Actions,
  ) {
  }

}
