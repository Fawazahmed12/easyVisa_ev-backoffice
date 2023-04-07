import { Injectable } from '@angular/core';

import { EMPTY, Observable } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';

import { RequestFailAction, RequestSuccessAction } from '../../../core/ngrx/utils';
import { UpdateOrganization } from '../../../core/ngrx/organizations/organizations.actions';
import { UpdateUserOrganizationProfile } from '../../../core/ngrx/user/user.actions';
import { Organization } from '../../../core/models/organization.model';

import { OrganizationProfile } from '../../profile/edit-preview-profile/models/organization-profile.model';
import {
  GetOrganization,
  GetOrganizationSuccess, OpenOrganizationSuccessChangingModal,
  OrganizationActionTypes, PostOrganizationPicture, PostOrganizationPictureSuccess,
  PutOrganization,
  PutOrganizationSuccess
} from './organization.actions';
import { organizationGetRequestHandler } from '../requests/organization-get/state';
import { organizationPutRequestHandler } from '../requests/organization-put/state';
import { organizationPicturePostRequestHandler } from '../requests/organization-picture-post/state';
import { ModalService } from '../../../core/services';
import { OkButton } from '../../../core/modals/confirm-modal/confirm-modal.component';

@Injectable()
export class OrganizationEffects {

  @Effect()
  getOrganization$: Observable<Action> = this.actions$.pipe(
    ofType(OrganizationActionTypes.GetOrganization),
    map(({ payload }: GetOrganization) => organizationGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getOrganizationSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(organizationGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<OrganizationProfile>) => new GetOrganizationSuccess(payload))
  );

  @Effect({ dispatch: false })
  getOrganizationFail$: Observable<Action> = this.actions$.pipe(
    ofType(organizationGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({ payload }: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  putOrganization$: Observable<Action> = this.actions$.pipe(
    ofType(OrganizationActionTypes.PutOrganization),
    map(({ payload }: PutOrganization) => organizationPutRequestHandler.requestAction(payload))
  );

  @Effect()
  putOrganizationSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(organizationPutRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<OrganizationProfile>) => new PutOrganizationSuccess(payload)),
    map(() => new OpenOrganizationSuccessChangingModal())
  );

  @Effect({ dispatch: false })
  putOrganizationFail$: Observable<Action> = this.actions$.pipe(
    ofType(organizationPutRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({ payload }: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  postOrganizationPicture$: Observable<Action> = this.actions$.pipe(
    ofType(OrganizationActionTypes.PostOrganizationPicture),
    map(({ payload }: PostOrganizationPicture) => organizationPicturePostRequestHandler.requestAction(payload))
  );

  @Effect()
  postOrganizationPictureSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(organizationPicturePostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<{ url: string }>) => new PostOrganizationPictureSuccess(payload.url))
  );

  @Effect({ dispatch: false })
  postOrganizationPictureFail$: Observable<Action> = this.actions$.pipe(
    ofType(organizationPicturePostRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({ payload }: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  updateOrganizationData$: Observable<Action> = this.actions$.pipe(
    ofType(OrganizationActionTypes.PutOrganizationSuccess),
    map(({ payload }: RequestSuccessAction<OrganizationProfile>) => new UpdateOrganization(new Organization(payload)))
  );

  @Effect()
  updateUserOrganizationData$: Observable<Action> = this.actions$.pipe(
    ofType(OrganizationActionTypes.PutOrganizationSuccess),
    map(({ payload }: RequestSuccessAction<OrganizationProfile>) => new UpdateUserOrganizationProfile(new Organization(payload)))
  );
  @Effect({ dispatch: false })
  openOrganizationSuccessChangingModal$: Observable<Action> = this.actions$.pipe(
    ofType(OrganizationActionTypes.OpenOrganizationSuccessChangingModal),
    switchMap(() => this.modalService.openConfirmModal({
        header: 'TEMPLATE.ACCOUNT.ORGANIZATION.SUCCESS_MODAL.TITLE',
        body: 'TEMPLATE.ACCOUNT.ORGANIZATION.SUCCESS_MODAL.O_1',
        centered: true,
        buttons: [OkButton],
      }).pipe(
        catchError(() => EMPTY)
      ))
  );

  constructor(
    private actions$: Actions, private modalService: ModalService
  ) {
  }

}
