import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { AuthModuleRequestService } from '../request.service';

export const representativeTypePostRequestHandler = createRequestHandler('representativeTypePostRequest');

export function representativeTypePostRequestReducer(state, action) {
  return representativeTypePostRequestHandler.reducer(state, action);
}

@Injectable()
export class RepresentativeTypePostRequestEffects {

  @Effect()
  representativeTypeData$: Observable<Action> = representativeTypePostRequestHandler.effect(
    this.actions$,
    this.authModuleRequestService.representativeTypePostRequest.bind(this.authModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private authModuleRequestService: AuthModuleRequestService,
  ) {
  }
}
