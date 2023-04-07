import { Injectable } from '@angular/core';

import { select, Store } from '@ngrx/store';

import { Observable } from 'rxjs';
import { filter, map, share } from 'rxjs/operators';

import { State } from '../ngrx/state';
import {
  getCurrentUser,
  getCurrentUserActiveMembership, getCurrentUserEasyVisaId,
  getCurrentUserPaidStatus, getCurrentUserRoles,
  getFindUserId, getIsCurrentRepresentativeMe, getIsEvRole,
  getRegistrationRepresentativeType
} from '../ngrx/user/user.state';
import { RequestState } from '../ngrx/utils';
import { throwIfRequestFailError } from '../ngrx/utils/rxjs-utils';
import {
  CancelMembership,
  CancelMembershipSuccess,
  ChangeUser,
  DeleteUser, GetUser,
  GetUserIdByEVId, PostConvertToAttorney,
  ReActivateMembership
} from '../ngrx/user/user.actions';
import {
  selectChangeMembershipPatchState,
  selectConvertToAttorneyPostState,
  selectUserDeleteState,
  selectUserIdByEVIdGetState
} from '../ngrx/user-requests/state';

import { User } from '../models/user.model';
import { Role } from '../models/role.enum';

import { PackagesService } from './packages.service';

export const authTokenKey = 'ev-auth-token';

@Injectable()
export class UserService {
  currentUser$: Observable<User>;
  currentUserEasyVisaId$: Observable<string>;
  currentUserRoles$: Observable<Role[]>;
  findUserId$: Observable<number>;
  activeMembership$: Observable<boolean>;
  paidStatus$: Observable<boolean>;
  registrationRepresentativeType$: Observable<string>;
  userIdByEVIdGetRequest$: Observable<RequestState<number>>;
  userDeleteRequest$: Observable<RequestState<number>>;
  changeMembershipPatchRequest$: Observable<RequestState<User>>;
  convertToAttorneyRequest$: Observable<RequestState<any>>;
  isCurrentRepresentativeMe$: Observable<boolean>;
  isEvRole$: Observable<boolean>;

  constructor(
    private store: Store<State>,
    private packageService: PackagesService,
  ) {
    this.currentUser$ = this.store.pipe(select(getCurrentUser));
    this.currentUserEasyVisaId$ = this.store.pipe(select(getCurrentUserEasyVisaId));
    this.currentUserRoles$ = this.store.pipe(select(getCurrentUserRoles)).pipe(
      filter((currentUserRoles) => !!currentUserRoles)
    );
    this.findUserId$ = this.store.pipe(select(getFindUserId));
    this.activeMembership$ = this.store.pipe(select(getCurrentUserActiveMembership));
    this.paidStatus$ = this.store.pipe(select(getCurrentUserPaidStatus));
    this.isCurrentRepresentativeMe$ = this.store.pipe(select(getIsCurrentRepresentativeMe));
    this.registrationRepresentativeType$ = this.store.pipe(select(getRegistrationRepresentativeType));
    this.userIdByEVIdGetRequest$ = this.store.pipe(select(selectUserIdByEVIdGetState));
    this.userDeleteRequest$ = this.store.pipe(select(selectUserDeleteState));
    this.changeMembershipPatchRequest$ = this.store.pipe(select(selectChangeMembershipPatchState));
    this.convertToAttorneyRequest$ = this.store.pipe(select(selectConvertToAttorneyPostState));
    this.isEvRole$ = this.store.pipe(select(getIsEvRole));
  }

  hasAccess(roles: Role[] = []) {
    return this.currentUser$.pipe(
      filter((user) => !!user),
      map((user) => user.roles.some((userRole: Role) => roles.includes(userRole))),
    );
  }

  hasActivePackage() {
    return this.packageService.activePackageId$.pipe(
      filter((activePackageId) => !!activePackageId),
      map((activePackageId) => !!activePackageId),
    );
  }

  getUserIdByEVId(params) {
    this.store.dispatch(new GetUserIdByEVId(params));
    return this.userIdByEVIdGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  deleteUser() {
    this.store.dispatch(new DeleteUser());
  }

  patchCancelMembership(params) {
    return this.changeMembership(new CancelMembership(params));
  }

  getRepresentativeBalance(params) {
    return this.changeMembership(new ReActivateMembership(params));
  }

  changeMembership(action: CancelMembership | ReActivateMembership) {
    this.store.dispatch(action);
    return this.changeMembershipPatchRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  cancelMembership(updatedUser: User) {
    return new CancelMembershipSuccess(updatedUser);
  }

  changeUser(updatedUser: User) {
    return new ChangeUser(updatedUser);
  }

  convertToAttorney(data) {
    this.store.dispatch(new PostConvertToAttorney(data));
    return this.convertToAttorneyRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  getUser(token) {
    this.store.dispatch(new GetUser(token));
  }
}
