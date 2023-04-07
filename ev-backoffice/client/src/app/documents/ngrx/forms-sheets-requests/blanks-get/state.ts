import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { FormsSheetsRequestService } from '../request.service';


export const blanksGetRequestHandler = createRequestHandler('BlanksGet');

export function blanksGetRequestReducer(state, action) {
  return blanksGetRequestHandler.reducer(state, action);
}

@Injectable()
export class BlanksGetRequestEffects {

  @Effect()
  blanksGet$: Observable<Action> = blanksGetRequestHandler.effect(
    this.actions$,
    this.formsSheetsRequestService.blanksGetRequest.bind(this.formsSheetsRequestService)
  );

  constructor(
    private actions$: Actions,
    private formsSheetsRequestService: FormsSheetsRequestService
  ) {
  }
}
