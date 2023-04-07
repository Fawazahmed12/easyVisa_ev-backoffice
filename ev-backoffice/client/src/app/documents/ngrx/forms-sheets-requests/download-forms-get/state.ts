import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { FormsSheetsRequestService } from '../request.service';


export const downloadFormsGetRequestHandler = createRequestHandler('DownloadFormsGet');

export function downloadFormsGetRequestReducer(state, action) {
  return downloadFormsGetRequestHandler.reducer(state, action);
}

@Injectable()
export class DownloadFormsGetRequestEffects {

  @Effect()
  downloadFormsGet$: Observable<Action> = downloadFormsGetRequestHandler.effect(
    this.actions$,
    this.formsSheetsRequestService.downloadFormsGetRequest.bind(this.formsSheetsRequestService)
  );

  constructor(
    private actions$: Actions,
    private formsSheetsRequestService: FormsSheetsRequestService
  ) {
  }
}
