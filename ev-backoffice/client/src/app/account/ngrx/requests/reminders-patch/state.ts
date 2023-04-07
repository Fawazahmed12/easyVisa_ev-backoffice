import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AccountModuleRequestService } from '../request.service';

export const remindersPatchRequestHandler = createRequestHandler('PatchRemindersRequest');

export function remindersPatchRequestReducer(state, action) {
  return remindersPatchRequestHandler.reducer(state, action);
}

@Injectable()
export class RemindersPatchRequestEffects {

  @Effect()
  remindersPatch$: Observable<Action> = remindersPatchRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.remindersPatchRequest.bind(this.accountModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
  ) {
  }
}
