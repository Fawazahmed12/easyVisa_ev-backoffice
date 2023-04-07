import { Injectable } from '@angular/core';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action, select, Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { map, switchMap, tap, withLatestFrom } from 'rxjs/operators';

import { head } from 'lodash-es';

import { RequestFailAction, RequestSuccessAction } from '../../../core/ngrx/utils';
import { ModalService } from '../../../core/services';

import { Review } from '../../models/review.model';

import { remindersGetRequestHandler, remindersPatchRequestHandler } from '../requests/state';
import { getFullNotificationTypes, State } from '../state';
import {
  GetReminders,
  GetRemindersFailure,
  GetRemindersSuccess,
  PatchReminders,
  PatchRemindersFailure,
  PatchRemindersSuccess,
  RemindersActionsUnion,
  RemindersActionTypes,
  SetActiveReminderType,
} from './reminders.actions';
import { OkButton } from '../../../core/modals/confirm-modal/confirm-modal.component';


@Injectable()
export class RemindersEffects {

  @Effect()
  GetReminders$: Observable<Action> = this.actions$.pipe(
    ofType(RemindersActionTypes.GetReminders),
    map(({ payload }: GetReminders) => remindersGetRequestHandler.requestAction(payload))
  );

  @Effect()
  GetRemindersSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(remindersGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<any>) => new GetRemindersSuccess(payload))
  );

  @Effect()
  GetRemindersFailure$: Observable<Action> = this.actions$.pipe(
    ofType(remindersGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new GetRemindersFailure(payload))
  );

  @Effect()
  PatchReminders$: Observable<Action> = this.actions$.pipe(
    ofType(RemindersActionTypes.PatchReminders),
    map(({ payload }: PatchReminders) => remindersPatchRequestHandler.requestAction(payload))
  );

  @Effect()
  PatchRemindersSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(remindersPatchRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<Review>) => new PatchRemindersSuccess(payload)),
    tap(({ payload }: RequestFailAction<any>) => {
      this.modalService.openConfirmModal({
          header: 'TEMPLATE.ACCOUNT.NOTIFICATIONS_REMINDERS.SUCCESS_MODAL.TITLE',
          body: 'TEMPLATE.ACCOUNT.NOTIFICATIONS_REMINDERS.SUCCESS_MODAL.P_1',
          buttons: [OkButton],
          centered: true,
        }
      );
    })
  );

  @Effect()
  PatchRemindersFailure$: Observable<Action> = this.actions$.pipe(
    ofType(remindersPatchRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new PatchRemindersFailure(payload))
  );

  @Effect({ dispatch: false })
  openRemindersFailModal$: Observable<Action> = this.actions$.pipe(
    ofType(
      RemindersActionTypes.PatchRemindersFailure,
      RemindersActionTypes.GetRemindersFailure,
    ),
    switchMap(({ payload }: RequestFailAction<any>) => this.modalService.showErrorModal(payload.error.errors || [payload.error]))
  );

  @Effect()
  setActiveDefaultReminder$: Observable<Action> = this.actions$.pipe(
    ofType(
      remindersGetRequestHandler.ActionTypes.REQUEST_SUCCESS,
    ),
    withLatestFrom(this.store.pipe(select(getFullNotificationTypes))),
    map(([, notifications]) => head(notifications?.deadline)?.value),
    switchMap((deadlineDefaultCategory) => [
        new SetActiveReminderType(deadlineDefaultCategory),
      ],
    )
  );

  constructor(
    private actions$: Actions<RemindersActionsUnion>,
    private modalService: ModalService,
    private store: Store<State>,
  ) {
  }
}
