import { Injectable } from '@angular/core';

import { EMPTY, Observable } from 'rxjs';
import { catchError, map, switchMap, tap, withLatestFrom } from 'rxjs/operators';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';

import { RequestFailAction, RequestSuccessAction } from '../../../core/ngrx/utils';
import { Profile } from '../../../core/models/profile.model';
import { UpdateUserProfile } from '../../../core/ngrx/user/user.actions';
import { ModalService, UserService } from '../../../core/services';
import { User } from '../../../core/models/user.model';
import { Role } from '../../../core/models/role.enum';
import { Attorney } from '../../../core/models/attorney.model';
import { Employee } from '../../../core/models/employee.model';
import { OkButton } from '../../../core/modals/confirm-modal/confirm-modal.component';

import { EmployeeProfile } from '../../profile/edit-preview-profile/models/employee-profile.model';
import { AttorneyProfile } from '../../profile/edit-preview-profile/models/attorney-profile.model';

import { profileEmailPutRequestHandler } from '../requests/state';
import { profileGetRequestHandler } from '../requests/profile-get/state';
import { profilePutRequestHandler } from '../requests/profile-put/state';
import { profilePicturePostRequestHandler } from '../requests/profile-picture-post/state';

import {
  GetProfileSuccess, OpenProfileSuccessChangingModal,
  PostProfilePicture,
  PostProfilePictureSuccess,
  ProfileActionTypes,
  PutProfile, PutProfileEmail, PutProfileEmailSuccess,
  PutProfileSuccess
} from './profile.actions';


@Injectable()
export class ProfileEffects {

  @Effect()
  getProfile$: Observable<Action> = this.actions$.pipe(
    ofType(ProfileActionTypes.GetProfile),
    map(() => profileGetRequestHandler.requestAction())
  );

  @Effect()
  getProfileSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(profileGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<AttorneyProfile & EmployeeProfile & Profile>) => new GetProfileSuccess(payload))
  );

  @Effect({dispatch: false})
  getProfileFail$: Observable<Action> = this.actions$.pipe(
    ofType(profileGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  putProfile$: Observable<Action> = this.actions$.pipe(
    ofType(ProfileActionTypes.PutProfile),
    map(({payload}: PutProfile) => profilePutRequestHandler.requestAction(payload))
  );

  @Effect()
  putProfileSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(profilePutRequestHandler.ActionTypes.REQUEST_SUCCESS),
    switchMap(({payload}: RequestSuccessAction<AttorneyProfile & EmployeeProfile & Profile>) => [
      new PutProfileSuccess(payload),
      new OpenProfileSuccessChangingModal()
    ])
  );

  @Effect({dispatch: false})
  putProfileFail$: Observable<Action> = this.actions$.pipe(
    ofType(profilePutRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect({dispatch: false})
  openProfileSuccessChangingModal$: Observable<Action> = this.actions$.pipe(
    ofType(ProfileActionTypes.OpenProfileSuccessChangingModal),
    switchMap(() => this.modalService.openConfirmModal({
        header: 'TEMPLATE.ACCOUNT.PROFILE.SUCCESS_MODAL.TITLE',
        body: 'TEMPLATE.ACCOUNT.PROFILE.SUCCESS_MODAL.P_1',
        centered: true,
        buttons: [OkButton],
      }).pipe(
        catchError(() => EMPTY)
      ))
  );

  @Effect()
  postProfilePicture$: Observable<Action> = this.actions$.pipe(
    ofType(ProfileActionTypes.PostProfilePicture),
    map(({payload}: PostProfilePicture) => profilePicturePostRequestHandler.requestAction(payload))
  );

  @Effect()
  postProfilePictureSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(profilePicturePostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<{ url: string }>) => new PostProfilePictureSuccess(payload.url))
  );

  @Effect({dispatch: false})
  postProfilePictureFail$: Observable<Action> = this.actions$.pipe(
    ofType(profilePicturePostRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  updateUserData$: Observable<Action> = this.actions$.pipe(
    ofType(ProfileActionTypes.PutProfileSuccess),
    withLatestFrom(this.userService.currentUser$),
    map(([{payload}, user]: [PutProfileSuccess, User]) => {
      let updatedProfile;
      if (user.roles.some((role: Role) => role === Role.ROLE_ATTORNEY)) {
        updatedProfile = new Attorney(payload);
      } else if (user.roles.some((role: Role) => role === Role.ROLE_EMPLOYEE)) {
        updatedProfile = new Employee(payload);
      }
      return new UpdateUserProfile(updatedProfile || payload);
    })
  );

  @Effect()
  PutProfileEmail$: Observable<Action> = this.actions$.pipe(
    ofType(ProfileActionTypes.PutProfileEmail),
    map(({payload}: PutProfileEmail) => profileEmailPutRequestHandler.requestAction(payload))
  );

  @Effect()
  PutProfileEmailSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(profileEmailPutRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<{ email: string }>) => new PutProfileEmailSuccess(payload.email))
  );

  @Effect({dispatch: false})
  PutProfileEmailSuccessModal$: Observable<Action> = this.actions$.pipe(
    ofType(ProfileActionTypes.PutProfileEmailSuccess),
    switchMap(() =>
      this.modalService.openConfirmModal({
        header: 'TEMPLATE.ACCOUNT.LOGIN_CREDENTIALS.EMAIL_UPDATED_TITLE',
        body: 'TEMPLATE.ACCOUNT.LOGIN_CREDENTIALS.EMAIL_UPDATED_BODY',
        centered: true,
        buttons: [OkButton],
      })
    )
  );

  @Effect({dispatch: false})
  PutProfileEmailFail$: Observable<Action> = this.actions$.pipe(
    ofType(profileEmailPutRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => this.modalService.showErrorModal(payload.error.errors || [payload.error]))
  );

  constructor(
    private actions$: Actions,
    private userService: UserService,
    private modalService: ModalService,
  ) {
  }

}
