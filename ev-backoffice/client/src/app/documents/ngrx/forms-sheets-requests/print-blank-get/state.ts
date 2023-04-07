import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { FormsSheetsRequestService } from '../request.service';


export const printBlankGetRequestHandler = createRequestHandler('PrintBlankGet');

export function printBlankGetRequestReducer(state, action) {
  return printBlankGetRequestHandler.reducer(state, action);
}

@Injectable()
export class PrintBlankGetRequestEffects {

  @Effect()
  printBlankGet$: Observable<Action> = printBlankGetRequestHandler.effect(
    this.actions$,
    this.formsSheetsRequestService.printBlankGetRequest.bind(this.formsSheetsRequestService)
  );

  constructor(
    private actions$: Actions,
    private formsSheetsRequestService: FormsSheetsRequestService
  ) {
  }
}
