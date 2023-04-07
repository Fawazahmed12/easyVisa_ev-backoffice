import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { catchError, map, mapTo, pluck, switchMap, tap, withLatestFrom } from 'rxjs/operators';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action, select, Store } from '@ngrx/store';

import { RequestFailAction, RequestSuccessAction } from '../../../../core/ngrx/utils';
import { ModalService, OrganizationService } from '../../../../core/services';

import { Invite } from '../../../models/invite.model';

import { PermissionsService } from '../../permissions.service';
import { OrganizationEmployee } from '../../models/organization-employee.model';

import { permissionsGetRequestHandler } from '../requests/permissions-get/state';
import { inviteMemberPutRequestHandler } from '../requests/invite-member-put/state';
import { permissionGetRequestHandler } from '../requests/permission-get/state';
import { updateEmployeePutRequestHandler } from '../requests/update-employee-put/state';
import { inviteDeleteRequestHandler } from '../requests/invite-delete/state';
import { getPermissionsEntities, State } from '../state';

import {
  DeleteInvite,
  DeleteInviteSuccess,
  GetPermission,
  GetPermissions,
  GetPermissionsSuccess,
  GetPermissionSuccess,
  PermissionsActionTypes,
  PutInviteMemberFailure,
  PutInviteMemberSuccess,
  PutUpdateEmployeeSuccess
} from './permissions.actions';
import { Dictionary } from '@ngrx/entity';

@Injectable()
export class PermissionsEffects {

  @Effect()
  getPermissions$: Observable<Action> = this.actions$.pipe(
    ofType(PermissionsActionTypes.GetPermissions),
    map(({ payload }: GetPermissions) => permissionsGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getPermissionsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(permissionsGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<OrganizationEmployee[]>) => new GetPermissionsSuccess(payload))
  );

  @Effect({ dispatch: false })
  getPermissionsFail$: Observable<Action> = this.actions$.pipe(
    ofType(permissionsGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({ payload }: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  getPermission$: Observable<Action> = this.actions$.pipe(
    ofType(PermissionsActionTypes.GetPermission),
    map(({ payload }: GetPermission) => permissionGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getPermissionSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(permissionGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<OrganizationEmployee>) => new GetPermissionSuccess(payload))
  );

  @Effect({ dispatch: false })
  getPermissionFail$: Observable<Action> = this.actions$.pipe(
    ofType(permissionGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({ payload }: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  putInviteMember$: Observable<Action> = this.actions$.pipe(
    ofType(PermissionsActionTypes.PutInviteMember),
    map(() => inviteMemberPutRequestHandler.requestAction())
  );

  @Effect()
  putInviteMemberSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(inviteMemberPutRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<any>) => new PutInviteMemberSuccess(payload))
  );

  @Effect()
  putInviteMemberFail$: Observable<Action> = this.actions$.pipe(
    ofType(inviteMemberPutRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestSuccessAction<any>) => new PutInviteMemberFailure(payload))
  );

  @Effect({ dispatch: false })
  openInvitationModal$: Observable<Action> = this.actions$.pipe(
    ofType(PermissionsActionTypes.PutInviteMemberSuccess),
    switchMap(({ payload }: RequestSuccessAction<Invite>) => this.permissionsService.openInviteSentModal(of(payload))),
    tap(() => this.router.navigate(['account', 'permissions'])),
  );

  @Effect()
  putUpdateEmployee$: Observable<Action> = this.actions$.pipe(
    ofType(PermissionsActionTypes.PutUpdateEmployee),
    map(() => updateEmployeePutRequestHandler.requestAction())
  );

  @Effect()
  putUpdateEmployeeSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(updateEmployeePutRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<any>) => new PutUpdateEmployeeSuccess(payload))
  );

  @Effect({ dispatch: false })
  putUpdateEmployeeFail$: Observable<Action> = this.actions$.pipe(
    ofType(updateEmployeePutRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({ payload }: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  DeleteInvite$: Observable<Action> = this.actions$.pipe(
    ofType(PermissionsActionTypes.DeleteInvite),
    pluck('payload'),
    withLatestFrom(this.organizationService.activeOrganizationId$),
    map(([employeeId, organizationId]) => inviteDeleteRequestHandler.requestAction({
      employeeId,
      organizationId
    }))
  );

  @Effect()
  DeleteInviteSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(inviteDeleteRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<any>) => new DeleteInviteSuccess(payload))
  );

  @Effect({ dispatch: false })
  DeleteInviteFail$: Observable<Action> = this.actions$.pipe(
    ofType(inviteDeleteRequestHandler.ActionTypes.REQUEST_FAIL),
    switchMap(({ payload }: RequestFailAction<any>) => this.modalService.showErrorModal(
          payload.error.errors || [payload.error] || payload.message
        )
    )
  );

  @Effect()
  OpenModal$: Observable<Action> = this.actions$.pipe(
    ofType(PermissionsActionTypes.OpenWithdrawInviteModal),
    pluck('payload'),
    withLatestFrom(this.store.pipe(select(getPermissionsEntities))),
    map(([id, entities]: [number, Dictionary<OrganizationEmployee>]) => entities[ id ]),
    switchMap((employee: OrganizationEmployee) =>
      this.permissionsService.openWithdrawInviteModal(employee).pipe(
        mapTo(employee.employeeId),
        catchError(() => EMPTY)
      )
    ),
    map((employeeId) => new DeleteInvite(employeeId)),
  );

  constructor(
    private actions$: Actions,
    private permissionsService: PermissionsService,
    private organizationService: OrganizationService,
    private modalService: ModalService,
    private router: Router,
    private store: Store<State>
  ) {
  }
}
