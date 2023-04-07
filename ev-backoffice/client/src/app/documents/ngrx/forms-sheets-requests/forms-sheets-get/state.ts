import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { FormsSheetsRequestService } from '../request.service';


export const formsSheetsGetRequestHandler = createRequestHandler('Forms Sheets Get');

export function formsSheetsGetRequestReducer(state, action) {
  return formsSheetsGetRequestHandler.reducer(state, action);
}

@Injectable()
export class FormsSheetsGetRequestEffects {

  @Effect()
  formsSheetsGet$: Observable<Action> = formsSheetsGetRequestHandler.effect(
    this.actions$,
    this.formsSheetsRequestService.formsSheetsGetRequest.bind(this.formsSheetsRequestService)
  );

  constructor(
    private actions$: Actions,
    private formsSheetsRequestService: FormsSheetsRequestService
  ) {
  }
}
