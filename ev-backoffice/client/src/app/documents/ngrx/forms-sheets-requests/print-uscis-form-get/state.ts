import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { FormsSheetsRequestService } from '../request.service';


export const printFormGetRequestHandler = createRequestHandler('PrintFormGet');

export function printFormGetRequestReducer(state, action) {
  return printFormGetRequestHandler.reducer(state, action);
}

@Injectable()
export class PrintFormGetRequestEffects {

  @Effect()
  printFormGet$: Observable<Action> = printFormGetRequestHandler.effect(
    this.actions$,
    this.formsSheetsRequestService.printFormGetRequest.bind(this.formsSheetsRequestService)
  );

  constructor(
    private actions$: Actions,
    private formsSheetsRequestService: FormsSheetsRequestService
  ) {
  }
}
