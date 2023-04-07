import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AccountModuleRequestService } from '../request.service';

export const nonRegisteredApplicantDeleteRequestHandler = createRequestHandler('DeleteNonRegisteredApplicant');

export function nonRegisteredApplicantDeleteRequestReducer(state, action) {
  return nonRegisteredApplicantDeleteRequestHandler.reducer(state, action);
}

@Injectable()
export class NonRegisteredApplicantDeleteRequestEffects {

  @Effect()
  nonRegisteredApplicantDelete$: Observable<Action> = nonRegisteredApplicantDeleteRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.deleteNonRegisteredApplicant.bind(this.accountModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
  ) {
  }
}
