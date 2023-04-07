import { Router } from '@angular/router';
import { Injectable } from '@angular/core';

import { Actions, Effect, ofType, ROOT_EFFECTS_INIT } from '@ngrx/effects';
import { Action, select, Store } from '@ngrx/store';

import { EMPTY, Observable } from 'rxjs';
import {
  catchError,
  debounceTime,
  filter,
  map,
  mapTo,
  pluck,
  switchMap,
  take,
  tap,
  withLatestFrom
} from 'rxjs/operators';
import { fromPromise } from 'rxjs/internal-compatibility';

import {
  CancelMembership,
  CancelMembershipSuccess,
  DeleteUserFailure,
  DeleteUserSuccess,
  GetTokenFailure,
  GetUser,
  GetUserFailure,
  GetUserIdByEVId, GetUserIdByEVIdFailure,
  GetUserIdByEVIdSuccess,
  GetUserSuccess,
  Login,
  LoginFailure,
  LoginInModal,
  LoginInModalSuccess,
  LoginSuccess,
  Logout,
  LogoutFailure,
  LogoutSuccess,
  PatchAttorneyFailure,
  PatchLoggedInAttorneySuccess,
  PostConvertToAttorney,
  PostConvertToAttorneyFailure,
  PostConvertToAttorneySuccess,
  ReActivateMembership,
  ReActivateMembershipSuccess,
  UserActionTypes
} from './user.actions';

import { representativeTypePostRequestHandler } from '../../../auth/ngrx/requests/representative-type-post/state';
import { completePaymentPostRequestHandler } from '../../../auth/ngrx/requests/complete-payment-post/state';

import { User } from '../../models/user.model';
import { AuthService, ModalService, UserService } from '../../services';
import { attorneyPatchRequestHandler } from '../representatives-requests/attorney-patch/state';
import { RegistrationStatus } from '../../models/registration-status.enum';
import { Attorney } from '../../models/attorney.model';
import { Role } from '../../models/role.enum';
import { AttorneyType } from '../../models/attorney-type.enum';
import { RepresentativeType } from '../../models/representativeType.enum';
import { LoginResponse } from '../../models/login-response.model';
import { OkButton } from '../../modals/confirm-modal/confirm-modal.component';

import { GetMenuOrganizations } from '../organizations/organizations.actions';
import { userGetRequestHandler } from '../user-requests/get/state';
import { loginRequestHandler } from '../auth-requests/login/state';
import { RequestFailAction, RequestSuccessAction } from '../utils';
import { logoutRequestHandler } from '../auth-requests/logout/state';
import { loginInModalRequestHandler } from '../auth-requests/login-in-modal/state';
import { UpdateRepresentative } from '../representatives/representatives.actions';
import { convertToAttorneyPostRequestHandler, userIdByEVIdGetRequestHandler } from '../user-requests/state';
import { userDeleteRequestHandler } from '../user-requests/delete-user/state';
import { changeMembershipPatchRequestHandler } from '../user-requests/change-membership/state';
import { State } from '../state';
import { OpenPackagesFailModals } from '../packages/packages.actions';
import { getRedirectUrl } from '../auth/auth.state';
import { ResetRedirectUrl } from '../auth/auth.actions';
import { GetTaskQueueCounts } from '../notifications/notifications.actions';
import { HttpErrorResponse } from '@angular/common/http';


@Injectable()
export class UserEffects {

  @Effect()
  login$: Observable<Action> = this.actions$.pipe(
    ofType(UserActionTypes.Login),
    map(({payload}: Login) => loginRequestHandler.requestAction(payload))
  );

  @Effect()
  loginSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(loginRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<LoginResponse>) => new LoginSuccess(payload))
  );

  @Effect()
  loginInModal$: Observable<Action> = this.actions$.pipe(
    ofType(UserActionTypes.LoginInModal),
    map(({payload}: LoginInModal) => loginInModalRequestHandler.requestAction(payload))
  );

  @Effect()
  loginInModalSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(loginInModalRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<LoginResponse>) => new LoginInModalSuccess(payload))
  );

  @Effect()
  getUserAfterLogin$: Observable<Action> = this.actions$.pipe(
    ofType(UserActionTypes.LoginSuccess),
    map(({payload}: RequestSuccessAction<LoginResponse>) => new GetUser(payload.access_token))
  );

  @Effect()
  getUserAfterLoginModal$: Observable<Action> = this.actions$.pipe(
    ofType(UserActionTypes.LoginInModalSuccess),
    map(({payload}: RequestSuccessAction<LoginResponse>) => new GetUser(payload.access_token))
  );

  @Effect()
  getUserAfterCreate$: Observable<Action> = this.actions$.pipe(
    ofType(UserActionTypes.PostUserSuccess),
    map(({payload}: RequestSuccessAction<LoginResponse>) => new GetUser(payload.access_token))
  );

  @Effect()
  loginFailure$: Observable<Action> = this.actions$.pipe(
    ofType(loginRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new LoginFailure(payload))
  );

  @Effect()
  getUser$: Observable<Action> = this.actions$.pipe(
    ofType(UserActionTypes.GetUser),
    map(({payload}: GetUser) => userGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getUserSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(userGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<User>) => new GetUserSuccess(payload))
  );


  @Effect()
  getUserFailure$: Observable<Action> = this.actions$.pipe(
    ofType(userGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new GetUserFailure(payload))
  );

  @Effect()
  getUserIdByEVId$: Observable<Action> = this.actions$.pipe(
    ofType(UserActionTypes.GetUserIdByEVId),
    map(({payload}: GetUserIdByEVId) => userIdByEVIdGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getUserIdByEVIdSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(userIdByEVIdGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<number>) => new GetUserIdByEVIdSuccess(payload))
  );

  @Effect()
  getUserIdByEVIdFailure$: Observable<Action> = this.actions$.pipe(
    ofType(userIdByEVIdGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<number>) => new GetUserIdByEVIdFailure(payload))
  );

  @Effect({dispatch: false})
  getUserFailureRedirect$: Observable<void> = this.actions$.pipe(
    ofType(
      UserActionTypes.GetUserFailure,
      UserActionTypes.LogoutSuccess,
      UserActionTypes.DeleteUserSuccess,
    ),
    switchMap(() =>
      fromPromise(this.router.navigate(['/auth', 'login']))
    ),
    map(() => {
      this.authService.removeAuthToken();
      this.authService.removeAllCookies();
    })
  );

  @Effect({dispatch: false})
  loginInModalSuccessRedirect: Observable<void> = this.actions$.pipe(
    ofType(
      UserActionTypes.LoginInModalSuccess,
    ),
    pluck('payload'),
    tap((user: User & { access_token: string }) => this.authService.setAuthToken(user.access_token)),
    withLatestFrom(this.store.pipe(select(getRedirectUrl))),
    map(([user, redirectUrl]: [User, string]) => {
      if (!!redirectUrl) {
        this.router.navigateByUrl(redirectUrl);
        this.store.dispatch(new ResetRedirectUrl());
      }
    }),
  );

  @Effect({dispatch: false})
  loginSuccessRedirect: Observable<boolean> = this.actions$.pipe(
    ofType(
      UserActionTypes.LoginSuccess,
    ),
    pluck('payload'),
    tap((user: User & { access_token: string }) => this.authService.setAuthToken(user.access_token)),
    withLatestFrom(this.store.pipe(select(getRedirectUrl))),
    map(([user, redirectUrl]: [User, string]) => {
      if (!!redirectUrl) {
        this.router.navigateByUrl(redirectUrl);
        this.store.dispatch(new ResetRedirectUrl());
      } else if (user.roles.some((role) => role === Role.ROLE_EMPLOYEE)) {
        this.router.navigate(['redirect-employee']);
      } else if (user.roles.some((role) => role !== Role.ROLE_USER)) {
        this.router.navigate(['dashboard', 'financial']);
      } else if (user.roles.some((role) => role === Role.ROLE_USER)) {
        this.router.navigate(['dashboard', 'progress-status']);
      } else {
        this.router.navigate(['account', 'profile']);
      }
      return true;
    }),
  );

  @Effect({dispatch: false})
  postUserSuccessRedirect: Observable<boolean> = this.actions$.pipe(
    ofType(
      UserActionTypes.PostUserSuccess,
    ),
    pluck('payload'),
    tap((user: User & { access_token: string }) => this.authService.setAuthToken(user.access_token)),
    map((user: User) => {
      if (user.roles.includes(Role.ROLE_USER)) {
        this.router.navigate(['auth', 'client-welcome']);
      } else {
        this.router.navigate(['redirect-employee']);
      }
      return true;
    }),
  );

  @Effect({dispatch: false})
  getUserSuccessRedirect: Observable<[User, string]> = this.actions$.pipe(
    ofType(
      UserActionTypes.GetUserSuccess,
    ),
    pluck('payload'),
    withLatestFrom(this.userService.registrationRepresentativeType$),
    tap(([user, representativeType]: [User, string]) => {
      // TODO: update condition considering Accredited Representative Role when backend would be ready
      if (user.roles.some((role) => role === Role.ROLE_ATTORNEY)) {
        this.redirectIfAttorney(user.profile as Attorney, representativeType);
      }
    }),
  );

  @Effect()
  setOrganizations$: Observable<Action> = this.actions$.pipe(
    ofType(
      UserActionTypes.GetUserSuccess,
      UserActionTypes.ReActivateMembershipSuccess,
      UserActionTypes.CancelMembershipSuccess,
    ),
    pluck('payload'),
    filter((user: User) => (user.roles.some((role) => role !== Role.ROLE_USER))),
    map(() =>
      new GetMenuOrganizations()
    ));

  @Effect()
  initialUserGet$: Observable<Action> = this.actions$.pipe(
    ofType(ROOT_EFFECTS_INIT),
    mapTo(this.authService.getAuthToken()),
    map((token) => {
      if (token) {
        return new GetUser(token);
      } else {
        return new GetTokenFailure();
      }
    }),
  );

  @Effect()
  patchAttorneySuccess$: Observable<Action> = this.actions$.pipe(
    ofType(
      attorneyPatchRequestHandler.ActionTypes.REQUEST_SUCCESS,
      completePaymentPostRequestHandler.ActionTypes.REQUEST_SUCCESS,
      representativeTypePostRequestHandler.ActionTypes.REQUEST_SUCCESS,
    ),
    switchMap(({payload}: RequestSuccessAction<Attorney>) =>
      this.userService.currentUser$.pipe(
        take(1),
        map((user) => {
          if (user.profile.id === payload.id) {
            return new PatchLoggedInAttorneySuccess({...user, profile: payload});
          } else {
            return new UpdateRepresentative(payload);
          }
        })
      )
    )
  );

  @Effect()
  patchAttorneyFailure$: Observable<Action> = this.actions$.pipe(
    ofType(
      attorneyPatchRequestHandler.ActionTypes.REQUEST_FAIL,
      representativeTypePostRequestHandler.ActionTypes.REQUEST_FAIL,
    ),
    map(({payload}: RequestFailAction<any>) => new PatchAttorneyFailure(payload))
  );

  @Effect({ dispatch: false })
  patchAttorneyFailureModal$: Observable<Action> = this.actions$.pipe(
    ofType(UserActionTypes.PatchAttorneyFailure),
    pluck('payload'),
    filter((error: HttpErrorResponse) => error && error.status !== 401),
    switchMap((error) => this.modalService.showErrorModal(error.error.errors || [error.error]))
  );

  @Effect()
  logout$: Observable<Action> = this.actions$.pipe(
    ofType(UserActionTypes.Logout),
    map(() => logoutRequestHandler.requestAction())
  );

  @Effect()
  logoutSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(logoutRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<void>) => new LogoutSuccess(payload))
  );

  @Effect()
  logoutFailure$: Observable<Action> = this.actions$.pipe(
    ofType(logoutRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestSuccessAction<void>) => new LogoutFailure(payload))
  );

  @Effect()
  deleteUser$: Observable<Action> = this.actions$.pipe(
    ofType(UserActionTypes.DeleteUser),
    map(({payload}: GetUser) => userDeleteRequestHandler.requestAction(payload))
  );

  @Effect()
  deleteUserSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(userDeleteRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<User>) => new DeleteUserSuccess(payload))
  );

  @Effect()
  deleteUserFailure$: Observable<Action> = this.actions$.pipe(
    ofType(userDeleteRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new DeleteUserFailure(payload))
  );

  @Effect({dispatch: false})
  deleteUserFailureShowModal$: Observable<Action> = this.actions$.pipe(
    ofType(UserActionTypes.DeleteUserFailure),
    switchMap(({payload}: RequestFailAction<any>) => this.modalService.showErrorModal(
      payload.error.errors || [payload.error] || payload.message)
    )
  );

  @Effect({dispatch: false})
  openShowOnlyPersonalDataShowModal$: Observable<Action> = this.actions$.pipe(
    ofType(UserActionTypes.ShowPersonalDataPopUp),
    debounceTime(500),
    switchMap(() => this.modalService.openConfirmModal({
        header: 'FORM.PERSONAL_PAGE_POP_UP.TITLE',
        body: 'FORM.PERSONAL_PAGE_POP_UP.P_1',
        buttons: [OkButton],
        centered: true,
      }).pipe(
      catchError(() => EMPTY)
      )
    )
  );

  @Effect()
  changeMembership$: Observable<Action> = this.actions$.pipe(
    ofType(
      UserActionTypes.ReActivateMembership,
      UserActionTypes.CancelMembership,
    ),
    map((payload: CancelMembership | ReActivateMembership) => changeMembershipPatchRequestHandler.requestAction(payload))
  );

  @Effect()
  changeMemberships$: Observable<Action> = this.actions$.pipe(
    ofType(changeMembershipPatchRequestHandler.ActionTypes.REQUEST_SUCCESS),
    pluck('payload'),
    map((payload: { actionType: string; user: User }) => {
      switch (payload.actionType) {
        case UserActionTypes.ReActivateMembership: {
          return new ReActivateMembershipSuccess(payload.user);
        }
        case UserActionTypes.CancelMembership: {
          return new CancelMembershipSuccess(payload.user);
        }
      }
    })
  );

  @Effect({dispatch: false})
  changeMembershipsFail$: Observable<Action> = this.actions$.pipe(
    ofType(changeMembershipPatchRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  postConvertToAttorney$: Observable<Action> = this.actions$.pipe(
    ofType(UserActionTypes.PostConvertToAttorney),
    map(({payload}: PostConvertToAttorney) => convertToAttorneyPostRequestHandler.requestAction(payload))
  );

  @Effect()
  postConvertToAttorneySuccess$: Observable<Action> = this.actions$.pipe(
    ofType(convertToAttorneyPostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<User>) => new PostConvertToAttorneySuccess(payload))
  );

  @Effect()
  postConvertToAttorneyFailure$: Observable<Action> = this.actions$.pipe(
    ofType(convertToAttorneyPostRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new PostConvertToAttorneyFailure(payload))
  );

  @Effect()
  postConvertToAttorneySuccessRedirect$: Observable<Action> = this.actions$.pipe(
    ofType(
      UserActionTypes.PostConvertToAttorneySuccess,
    ),
    map(({payload}: RequestSuccessAction<any>) => new GetUser(payload.access_token)),
    debounceTime(500),
    tap(() => this.router.navigate(['account', 'profile'], {queryParams: {setActiveSolo: true}}))
  );

  @Effect()
  postConvertToAttorneyFail$: Observable<Action> = this.actions$.pipe(
    ofType(UserActionTypes.PostConvertToAttorneyFailure),
    map(({payload}: RequestFailAction<any>) => new OpenPackagesFailModals(payload.error.errors))
  );

  @Effect()
  getTaskQueueCountsForUser$: Observable<Action> = this.actions$.pipe(
    ofType(UserActionTypes.GetUserSuccess),
    filter(({payload}: GetUserSuccess) => {
      const userRoles = payload.roles;
      return !!userRoles.find(role => role === Role.ROLE_USER);
    }),
    map(() => new GetTaskQueueCounts(null))
  );

  constructor(
    private actions$: Actions,
    private router: Router,
    private authService: AuthService,
    private userService: UserService,
    private modalService: ModalService,
    private store: Store<State>,
  ) {
  }

  redirectIfAttorney(attorney: Attorney, representativeType: string) {
    switch (attorney.registrationStatus) {
      case RegistrationStatus.EMAIL_VERIFIED: {
        this.router.navigate(['auth', 'standard-ev-charges']);
        break;
      }
      case RegistrationStatus.REPRESENTATIVE_SELECTED: {
          this.router.navigate(['auth', 'standard-ev-charges']);
        break;
      }
      case RegistrationStatus.CONTACT_INFO_UPDATED: {
        this.router.navigate(['auth', 'pay-sign-up-fee']);
        break;
      }
    }
  }
}
