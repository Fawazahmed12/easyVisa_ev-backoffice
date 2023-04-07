import { Injectable } from '@angular/core';

import { select, Store } from '@ngrx/store';

import { Observable } from 'rxjs';
import { filter, map, publishReplay, refCount, share } from 'rxjs/operators';
import { fromPromise } from 'rxjs/internal-compatibility';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { createEmployeePostRequestHandler } from './ngrx/requests/create-employee-post/state';
import { Dictionary } from '@ngrx/entity';

import { State } from '../../core/ngrx/state';
import { RequestState } from '../../core/ngrx/utils';
import { throwIfRequestFailError } from '../../core/ngrx/utils/rxjs-utils';

import { InvitationRequestSentComponent } from '../modals/invitation-request-sent/invitation-request-sent.component';

import {
  getActivePermission, getActivePermissionId,
  getCreateEmployeePostRequestState, getInviteDeleteRequestState,
  getInviteMemberPutRequestState, getPermissionGetRequestState,
  getPermissions, getPermissionsEntities,
  getPermissionsGetRequestState,
  getUpdateEmployeePutRequestState,
  getVerifyMemberPostRequestState
} from './ngrx/state';
import { inviteMemberPutRequestHandler } from './ngrx/requests/invite-member-put/state';
import { verifyMemberPostRequestHandler } from './ngrx/requests/verify-member-post/state';
import { GetPermission, GetPermissions, OpenWithdrawInviteModal } from './ngrx/permissions/permissions.actions';

import { OrganizationEmployee } from './models/organization-employee.model';
import { updateEmployeePutRequestHandler } from './ngrx/requests/update-employee-put/state';
import { WithdrawInviteComponent } from './modals/withdraw-invite/withdraw-invite.component';


@Injectable()
export class PermissionsService {
  permissions$: Observable<OrganizationEmployee[]>;
  activePermission$: Observable<OrganizationEmployee>;
  activePermissionId$: Observable<number>;
  permissionsEntities$: Observable<Dictionary<OrganizationEmployee>>;
  inviteMemberPutState$: Observable<RequestState<any>>;
  verifyMemberPostState$: Observable<RequestState<any>>;
  createEmployeePostState$: Observable<RequestState<any>>;
  updateEmployeePutState$: Observable<RequestState<any>>;
  getPermissionsGetState$: Observable<RequestState<OrganizationEmployee[]>>;
  getPermissionGetState$: Observable<RequestState<OrganizationEmployee>>;
  inviteDeleteState$: Observable<RequestState<any>>;

  constructor(
    private store: Store<State>,
    private ngbModal: NgbModal
  ) {
    this.permissions$ = this.store.pipe(select(getPermissions));
    this.activePermission$ = this.store.pipe(select(getActivePermission));
    this.activePermissionId$ = this.store.pipe(select(getActivePermissionId));
    this.permissions$ = this.store.pipe(select(getPermissions));
    this.permissionsEntities$ = this.store.pipe(select(getPermissionsEntities));
    this.inviteMemberPutState$ = this.store.pipe(select(getInviteMemberPutRequestState));
    this.getPermissionGetState$ = this.store.pipe(select(getPermissionGetRequestState));
    this.verifyMemberPostState$ = this.store.pipe(select(getVerifyMemberPostRequestState));
    this.getPermissionsGetState$ = this.store.pipe(select(getPermissionsGetRequestState));
    this.createEmployeePostState$ = this.store.pipe(select(getCreateEmployeePostRequestState));
    this.updateEmployeePutState$ = this.store.pipe(select(getUpdateEmployeePutRequestState));
    this.inviteDeleteState$ = this.store.pipe(select(getInviteDeleteRequestState));
  }

  inviteMember(memberDetails) {
    this.store.dispatch(inviteMemberPutRequestHandler.requestAction(memberDetails));
    return this.inviteMemberPutState$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  verifyMember(memberDetails) {
    this.store.dispatch(verifyMemberPostRequestHandler.requestAction(memberDetails));
    return this.verifyMemberPostState$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  getPermissions(data) {
    this.store.dispatch(new GetPermissions(data));
    return this.getPermissionsGetState$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  getPermission(data) {
    return this.permissionsEntities$.pipe(
      map((entities) => entities[data] ? entities[data] : this.store.dispatch(new GetPermission(data))),
      publishReplay(1),
      refCount()
    );
  }

  createEmployee(employeeDetails) {
    this.store.dispatch(createEmployeePostRequestHandler.requestAction(employeeDetails));
    return this.createEmployeePostState$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  updateEmployee(employeeDetails) {
    this.store.dispatch(updateEmployeePutRequestHandler.requestAction(employeeDetails));
    return this.updateEmployeePutState$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  openInviteSentModal(invite) {
    const modalRef = this.ngbModal.open(InvitationRequestSentComponent, {
      centered: true,
      size: 'lg'
    });
    modalRef.componentInstance.isRequest = false;
    modalRef.componentInstance.invite = invite;
    return fromPromise(modalRef.result);
  }

  openWithdrawInviteModal(employee) {
    const modalRef = this.ngbModal.open(WithdrawInviteComponent, {
      centered: true,
    });
    modalRef.componentInstance.employee = employee;
    return fromPromise(modalRef.result);
  }

  openWithdrawInviteModalAction(data) {
    this.store.dispatch(new OpenWithdrawInviteModal(data));
  }
}
