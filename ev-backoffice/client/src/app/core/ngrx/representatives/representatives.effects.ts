import { Injectable } from '@angular/core';

import { filter, map, pluck, switchMap, tap, withLatestFrom } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { RequestFailAction, RequestSuccessAction } from '../utils';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action, select, Store } from '@ngrx/store';

import { head } from 'lodash-es';

import { WarningsActionTypes } from '../../../task-queue/ngrx/warnings/warnings.actions';
import { DispositionsActionTypes } from '../../../task-queue/ngrx/dispositions/dispositions.actions';

import { OrganizationService, UserService } from '../../services';

import { User } from '../../models/user.model';
import { Attorney } from '../../models/attorney.model';
import { Role } from '../../models/role.enum';

import { AlertsActionTypes } from '../alerts/alerts.actions';
import { representativesGetRequestHandler } from '../representatives-requests/representatives-get/state';
import { UserActionTypes } from '../user/user.actions';
import { GetTaskQueueCounts } from '../notifications/notifications.actions';
import { representativesMenuGetRequestHandler } from '../representatives-requests/representatives-menu-get/state';

import {
  GetFeeSchedule,
  GetFeeScheduleSuccess,
  GetRepresentatives, GetRepresentativesMenuSuccess,
  GetRepresentativesSuccess,
  RepresentativesActionTypes,
  SetCurrentRepresentativeId, SetCurrentRepresentativeIdToLocalStorage, UpdateCurrentRepresentativeId,
} from './representatives.actions';
import { SelectorValues } from '../../models/selector.enum';
import { FeeSchedule } from '../../models/fee-schedule.model';

import { OrganizationsActionTypes } from '../organizations/organizations.actions';
import { feeScheduleGetRequestHandler } from '../representatives-requests/state';
import { State } from '../state';
import { getIsAdmin } from '../organizations/organizations.state';
import { OrganizationType } from '../../models/organization-type.enum';


@Injectable()
export class RepresentativesEffects {
  @Effect()
  getRepresentatives$: Observable<Action> = this.actions$.pipe(
    ofType(RepresentativesActionTypes.GetRepresentatives),
    map(({payload}: GetRepresentatives) => representativesGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getRepresentativesSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(representativesGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<Attorney[]>) => new GetRepresentativesSuccess(payload))
  );

  @Effect({dispatch: false})
  getRepresentativesFail$: Observable<Action> = this.actions$.pipe(
    ofType(representativesGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  getFeeSchedule$: Observable<Action> = this.actions$.pipe(
    ofType(RepresentativesActionTypes.GetFeeSchedule),
    filter(({payload}: GetFeeSchedule) => !!payload),
    map(({payload}: GetFeeSchedule) => feeScheduleGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getFeeScheduleSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(feeScheduleGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<FeeSchedule>) => new GetFeeScheduleSuccess(payload))
  );

  @Effect({dispatch: false})
  getFeeScheduleFail$: Observable<Action> = this.actions$.pipe(
    ofType(feeScheduleGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  getRepresentativesMenu$: Observable<Action> = this.actions$.pipe(
    ofType(RepresentativesActionTypes.GetRepresentativesMenu),
    map(({payload}: GetRepresentatives) => representativesMenuGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getRepresentativesMenuSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(representativesMenuGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<Attorney[]>) => new GetRepresentativesMenuSuccess(payload))
  );

  @Effect({dispatch: false})
  getRepresentativesMenuFail$: Observable<Action> = this.actions$.pipe(
    ofType(representativesMenuGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  setCurrentRepresentativeId$: Observable<Action> = this.actions$.pipe(
    ofType(
      RepresentativesActionTypes.GetRepresentativesMenuSuccess,
    ),
    withLatestFrom(
      this.userService.currentUser$,
      this.organizationService.activeOrganization$.pipe(
        filter(activeOrganization => !!activeOrganization),
        pluck('organizationType'),
      )
    ),
    switchMap(([action, currentUser, orgType]) => this.organizationService.getCurrentRepresentativeId().pipe(
      map(localCurrentUserId => [action, currentUser, localCurrentUserId, orgType])
    )),
    map(([action, currentUser, localCurrentUserId, orgType]: [GetRepresentativesSuccess, User, string, OrganizationType]) => {

      const representatives: Attorney[] = action.payload;
      const firstRepId = head(representatives).id;
      const profile: Attorney = currentUser.profile as Attorney;
      const isValidFromLs = this.isValidRepresentativeId(localCurrentUserId, representatives);
      const isRepInSelector = this.isValidRepresentativeId(profile.id, representatives);

      if (localCurrentUserId !== SelectorValues.SHOW_ALL && localCurrentUserId !== '' && isValidFromLs) {
        return new SetCurrentRepresentativeId(parseInt(localCurrentUserId, 10));
      } else if (localCurrentUserId === SelectorValues.SHOW_ALL && orgType !== OrganizationType.SOLO_PRACTICE) {
        return new SetCurrentRepresentativeId(null);
      } else if ((currentUser.roles.some((role) => role !== Role.ROLE_EMPLOYEE)) && representatives.length === 1) {
        const [singleRepresentative] = representatives;
        return new SetCurrentRepresentativeId(singleRepresentative && singleRepresentative.id);
      } else if (!isValidFromLs && !isRepInSelector) {
        return new SetCurrentRepresentativeId(firstRepId);
      } else if (currentUser.roles.some((role) => role !== Role.ROLE_EMPLOYEE)) {
        return new SetCurrentRepresentativeId(profile.id);
      } else if (currentUser.roles.some((role) => role === Role.ROLE_EMPLOYEE)) {
        return new SetCurrentRepresentativeId(isRepInSelector ? profile.id : firstRepId);
      }
      return new SetCurrentRepresentativeId(null);
    })
  );

  @Effect({dispatch: false})
  SetCurrentRepresentativeIdToLocalStorage$: Observable<Action> = this.actions$.pipe(
    ofType(
      RepresentativesActionTypes.SetCurrentRepresentativeId,
      RepresentativesActionTypes.UpdateCurrentRepresentativeId
    ),
    tap(({payload}: SetCurrentRepresentativeIdToLocalStorage) => {
      this.organizationService.setCurrentRepresentativeId(payload === null ? SelectorValues.SHOW_ALL : payload);
    })
  );

  @Effect()
  getFeeScheduleApp$: Observable<Action> = this.actions$.pipe(
    ofType(RepresentativesActionTypes.UpdateCurrentRepresentativeId),
    withLatestFrom(this.store.pipe(select(getIsAdmin))),
    filter(([, isAdmin]) => isAdmin),
    map(([{payload},]: [any, boolean]) => new GetFeeSchedule(payload))
  );

  @Effect()
  getTaskQueueCounts$: Observable<Action> = this.actions$.pipe(
    ofType(
      WarningsActionTypes.GetWarningsSuccess,
      AlertsActionTypes.GetAlertsSuccess,
      DispositionsActionTypes.GetDispositionsSuccess,
    ),
    withLatestFrom(
      this.organizationService.currentRepIdOrgId$,
    ),
    map(([, [representativeId, organizationId]]: [SetCurrentRepresentativeId | UpdateCurrentRepresentativeId, number[]]) =>
      new GetTaskQueueCounts({representativeId, organizationId}))
  );

  @Effect({dispatch: false})
  RemoveCurrentRepresentativeIdFromLocalStorage$: Observable<Action> = this.actions$.pipe(
    ofType(
      OrganizationsActionTypes.ChangeActiveOrganization,
      UserActionTypes.Logout
    ),
    tap(() => {
      this.organizationService.removeCurrentRepresentativeId();
    })
  );

  private isValidRepresentativeId(id, representatives) {
    const isValid = !!representatives.find((representative) => representative.id === +id);
    return isValid;
  }

  constructor(
    private actions$: Actions,
    private organizationService: OrganizationService,
    private userService: UserService,
    private store: Store<State>,
  ) {
  }
}
