import { Injectable } from '@angular/core';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action, select, Store } from '@ngrx/store';

import { filter, map, skip, switchMap, take, tap, withLatestFrom } from 'rxjs/operators';
import { Observable, of } from 'rxjs';

import { PackageModalType } from '../../../task-queue/models/package-modal-type.enum';
import { SetNonRegisteredApplicants } from '../../../account/ngrx/non-registered-applicants/non-registered-applicants.action';

import { Package } from '../../models/package/package.model';
import { ModalService, PackagesService } from '../../services';
import { ProcessRequestState } from '../../models/process-request-state.enum';
import { OkButton } from '../../modals/confirm-modal/confirm-modal.component';

import { RequestFailAction, RequestSuccessAction } from '../utils';
import {
  activePackageGetRequestHandler,
  packagesTransferByApplicantPostRequestHandler,
  packagesTransferPostRequestHandler
} from '../packages-requests/state';
import { packagesGetRequestHandler } from '../packages-requests/packages-get/state';
import { UserActionTypes } from '../user/user.actions';
import { OrganizationsActionTypes } from '../organizations/organizations.actions';
import { State } from '../state';
import { RepresentativesActionTypes } from '../representatives/representatives.actions';

import {
  ClearActivePackage,
  GetActivePackage,
  GetActivePackageFailure,
  GetActivePackageSuccess, GetPackagesSuccess, OpenPackagesFailModals, OpenRequestTransferSentModal,
  PackagesActionTypes, PatchPackageSuccess, PostPackageSuccess, SetActivePackageId
} from './packages.actions';
import { selectCurrentPackage } from './packages.state';


@Injectable()
export class PackagesEffects {

  @Effect()
  getActivePackage$: Observable<Action> = this.actions$.pipe(
    ofType(PackagesActionTypes.GetActivePackage),
    switchMap(({payload}: GetActivePackage) => this.packagesService.packageEntities$.pipe(
      map((entities) => {
        const foundActivePackage = entities[payload];
        return entities[payload] ? new GetActivePackageSuccess(foundActivePackage) : activePackageGetRequestHandler.requestAction(payload);
      }),
      take(1),
    ))
  );

  @Effect()
  getActivePackageSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(activePackageGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<Package>) => new GetActivePackageSuccess(payload))
  );

  @Effect()
  getActivePackageFailure$: Observable<Action> = this.actions$.pipe(
    ofType(activePackageGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new GetActivePackageFailure(payload))
  );

  @Effect()
  getPackages$: Observable<Action> = this.actions$.pipe(
    ofType(PackagesActionTypes.GetPackages),
    map((payload) => packagesGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getPackagesSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(packagesGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<Package[]>) => new GetPackagesSuccess(payload))
  );

  @Effect({dispatch: false})
  openNoResults$: Observable<Action> = this.actions$.pipe(
    ofType(PackagesActionTypes.GetPackagesSuccess),
    switchMap(({payload}: RequestSuccessAction<Package[]>) => payload.body.length === 0 && payload.isShowModal ?
      this.packagesService.noResultsModal(payload.isEditPackagePage)
      : of(true)
    )
  );

  @Effect()
  setActivePackageAfterCreateEditPackage$: Observable<Action> = this.actions$.pipe(
    ofType(
      PackagesActionTypes.PostPackageSuccess,
      PackagesActionTypes.PatchPackageSuccess,
    ),
    map(({payload}: PostPackageSuccess | PatchPackageSuccess) => new SetActivePackageId(payload.id))
  );

  @Effect()
  setActivePackageAfterLogin$: Observable<Action> = this.actions$.pipe(
    ofType(UserActionTypes.GetUserSuccess),
    switchMap(() => this.packagesService.getActivePackageId()),
    filter((id) => !!id),
    map((id) => new SetActivePackageId(id))
  );

  @Effect()
  getActivePackageAfterSetActivePackageId$: Observable<Action> = this.actions$.pipe(
    ofType(PackagesActionTypes.SetActivePackageId),
    map(({payload}: SetActivePackageId) => payload ? new GetActivePackage(payload) : new ClearActivePackage())
  );

  @Effect()
  clearActivePackageId$: Observable<Action> = this.actions$.pipe(
    ofType(
      OrganizationsActionTypes.ChangeActiveOrganization
    ),
    map(() => new ClearActivePackage())
  );

  @Effect()
  clearActivePackageAfterRepChanges$: Observable<Action> = this.actions$.pipe(
    ofType(
      RepresentativesActionTypes.UpdateCurrentRepresentativeId
    ),
    skip(1),
    map(() => new ClearActivePackage())
  );

  @Effect({dispatch: false})
  clearActivePackageIdFromLocalStorage$: Observable<Action> = this.actions$.pipe(
    ofType(
      UserActionTypes.Logout,
      PackagesActionTypes.ClearActivePackage,
    ),
    tap(() => this.packagesService.removeActivePackageId()),
  );

  @Effect({dispatch: false})
  setActivePackageIdToLocalStorage$: Observable<Action> = this.actions$.pipe(
    ofType(PackagesActionTypes.GetActivePackage),
    tap(({payload}: GetActivePackage) => this.packagesService.setActivePackageId(payload)),
  );

  @Effect({dispatch: false})
  openConflictBenefitCategoryModal$: Observable<Action> = this.actions$.pipe(
    ofType(PackagesActionTypes.OpenConflictBenefitCategoryModal),
    tap(({payload}: RequestFailAction<any>) => {
      const isImmigrationCategoryConflict = payload.error.errors.find(
        (err) => err.type === PackageModalType.IMMIGRATION_CATEGORY_CONFLICT);
      if (!!isImmigrationCategoryConflict) {
        this.packagesService.openBenefitCategoryConflictModal(isImmigrationCategoryConflict.message);
      }
    }),
  );

  @Effect({dispatch: false})
  openRequestTransferSentModal$: Observable<Action> = this.actions$.pipe(
    ofType(PackagesActionTypes.OpenRequestTransferSentModal),
    tap(({payload}: any) => this.packagesService.openTransferCaseRequestSentModal(payload)),
  );

  @Effect()
  getApplicants$: Observable<Action> = this.actions$.pipe(
    ofType(PackagesActionTypes.GetApplicants),
    switchMap(({payload}: GetActivePackage) => this.packagesService.packageEntities$.pipe(
      map((entities) => entities[payload].applicants.filter((applicant) => !applicant.register))
    )),
    map((applicants) => new SetNonRegisteredApplicants(applicants))
  );

  @Effect({dispatch: false})
  openPackagesFailModals$ = this.actions$.pipe(
    ofType(PackagesActionTypes.OpenPackagesFailModals),
    filter((action: OpenPackagesFailModals) => !!action.payload),
    map((action: OpenPackagesFailModals) => Array.isArray(action.payload) ? action.payload : [action.payload]),
    withLatestFrom(this.store.pipe(select(selectCurrentPackage))),
    tap(([errors, item]: [{
        message?: string;
        text?: string;
        type?: PackageModalType;
        status?: string;
        code?: number;
      }[], Package]) => {
        const isUnauthorized = errors.some(error => error.code === 401);
        if (isUnauthorized) {
          return;
        }
        return errors.forEach((error) => {
          if (error?.type) {
            switch (error.type) {
              case PackageModalType.MEMBERS_OF_BLOCKED_PACKAGE: {
                return this.packagesService.openCannotConvertPackageModal(item);
              }
              case PackageModalType.MEMBERS_WITH_PENDING_OR_DENY_STATUS: {
                const pendingOptApplicants = item.applicants.filter((applicant) => applicant.optIn === ProcessRequestState.PENDING);
                const denyOptApplicants = item.applicants.filter((applicant) => applicant.optIn === ProcessRequestState.DECLINED);
                return this.packagesService.openPackageCannotBeOpenModal(pendingOptApplicants, denyOptApplicants);
              }
              case PackageModalType.IMMIGRATION_CATEGORY_CONFLICT: {
                return this.packagesService.openBenefitCategoryConflictModal(error.message);
              }
              case PackageModalType.BLOCKED_OPEN_PACKAGES: {
                return this.packagesService.openInBlockedOrOpenPackageModal();
              }
              case PackageModalType.REMINDER_APPLICANT_INVITATION: {
                return this.packagesService.openReminderInvitationRegisterModal();
              }
              case PackageModalType.REMINDER_APPLICANT_PERMISSION: {
                return this.packagesService.openReminderApplicantPermissionModal();
              }
              case PackageModalType.PAYMENT_FAILED: {
                return this.packagesService.openPaymentFailedModal(error.message);
              }
              case PackageModalType.PAYMENT_CHARGED: {
                return this.modalService.openConfirmModal({
                  header: 'TEMPLATE.MODAL.PAYMENT_TITLE',
                  body: error.text,
                  buttons: [OkButton],
                });
              }
              default: {
                return this.modalService.showErrorModal(error.message);
              }
            }
          } else {
            this.modalService.showErrorModal(error?.message || error);
          }
        });
      }
    ),
  );

  @Effect()
  packagesTransferSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(
      packagesTransferPostRequestHandler.ActionTypes.REQUEST_SUCCESS,
      packagesTransferByApplicantPostRequestHandler.ActionTypes.REQUEST_SUCCESS,
    ),
    map(({payload}: RequestSuccessAction<any>) => new OpenRequestTransferSentModal(payload))
  );

  @Effect({dispatch: false})
  packagesTransferFailure$: Observable<Action> = this.actions$.pipe(
    ofType(
      packagesTransferPostRequestHandler.ActionTypes.REQUEST_FAIL,
      packagesTransferByApplicantPostRequestHandler.ActionTypes.REQUEST_FAIL,
    ),
    switchMap(
      ({payload}: RequestFailAction<any>) => this.modalService.showErrorModal(payload.error.errors || [payload.error]))
  );

  constructor(
    private actions$: Actions,
    private packagesService: PackagesService,
    private modalService: ModalService,
    private store: Store<State>,
  ) {
  }
}
