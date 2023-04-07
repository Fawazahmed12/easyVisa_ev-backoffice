import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { FormsSheetsRequestService } from '../request.service';


export const downloadBlanksGetRequestHandler = createRequestHandler('DownloadBlanksGet');

export function downloadBlanksGetRequestReducer(state, action) {
  return downloadBlanksGetRequestHandler.reducer(state, action);
}

@Injectable()
export class DownloadBlanksGetRequestEffects {

  @Effect()
  downloadBlanksGet$: Observable<Action> = downloadBlanksGetRequestHandler.effect(
    this.actions$,
    this.formsSheetsRequestService.downloadBlanksGetRequest.bind(this.formsSheetsRequestService)
  );

  constructor(
    private actions$: Actions,
    private formsSheetsRequestService: FormsSheetsRequestService
  ) {
  }
}
