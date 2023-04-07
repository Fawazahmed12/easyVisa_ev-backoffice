import {Injectable} from '@angular/core';

import {catchError, filter, share} from 'rxjs/operators';
import {EMPTY, Observable, of, Subject} from 'rxjs';

import {select, Store} from '@ngrx/store';
import {Dictionary} from '@ngrx/entity/src/models';

import {Email} from '../models/email.model';
import {emailPostRequestHandler, selectEmailPostRequestState} from '../ngrx/emails-requests/state';
import {throwIfRequestFailError} from '../ngrx/utils/rxjs-utils';
import {PackageApplicant} from '../models/package/package-applicant.model';
import {Package} from '../models/package/package.model';
import {PackageStatus} from '../models/package/package-status.enum';
// TODO: fix importing of packages state
import {
  deleteLeadPackagesRequestState,
  deleteSelectedLeadPackagesRequestState,
  deleteSelectedTransferredPackagesRequestState,
  getApplicantInvitePostRequestState,
  getApplicantRequestState,
  getChangePackageOwedPatchRequestState,
  getChangePackageStatusPostRequestState,
  getFeesBillPostRequestState,
  getPackageRequestState,
  getRetainerAgreementDeleteRequestState,
  getRetainerAgreementPostRequestState,
  patchPackageRequestState,
  postPackageRequestState,
  postPackageWelcomeEmailRequestState,
  State
} from '../../task-queue/ngrx/state';
import {
  ChangePackageOwed,
  ChangePackageStatus,
  ClearActivePackage,
  GetPackage,
  GetPackages,
  PatchPackage,
  PatchPackageWithoutReminder,
  PostPackage,
  RemovePackage,
  RemovePackages,
  SelectPackageId,
  SetActivePackageId,
  UpdatePackage,
} from '../ngrx/packages/packages.actions';
import {applicantGetRequestHandler} from '../../task-queue/ngrx/requests/applicant-get/state';
import {retainerAgreementPostRequestHandler} from '../../task-queue/ngrx/requests/retainer-agreement-post/state';
import {packageWelcomeEmailPostRequestHandler} from '../../task-queue/ngrx/requests/package-welcome-email-post/state';
import {applicantInvitePostRequestHandler} from '../../task-queue/ngrx/requests/applicant-invite-post/state';
import {retainerAgreementDeleteRequestHandler} from '../../task-queue/ngrx/requests/retainer-agreement-delete/state';
import {deleteLeadPackagesRequestHandler} from '../../task-queue/ngrx/requests/lead-packages-delete/state';
import {
  deleteSelectedLeadPackagesRequestHandler
} from '../../task-queue/ngrx/requests/selected-lead-packages-delete/state';
import {
  deleteSelectedTransferredPackagesRequestHandler
} from '../../task-queue/ngrx/requests/selected-transferred-packages-delete/state';
import {feesBillPostRequestHandler} from '../../task-queue/ngrx/requests/state';

import {RequestState} from '../ngrx/utils';
import {
  getActivePackage,
  getActivePackageId,
  getPackagesData,
  getPackagesEntities,
  getPackagesTotal,
  selectCurrentPackage
} from '../ngrx/packages/packages.state';
import {
  packagesTransferByApplicantPostRequestHandler,
  packagesTransferPostRequestHandler,
  selectPackagesGetRequestState,
  selectTransferByApplicantPostRequestState,
  selectTransferPostRequestState
} from '../ngrx/packages-requests/state';


import {CookieService} from 'ngx-cookie-service';
import {fromPromise} from 'rxjs/internal-compatibility';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

import {
  RequestTransferSentModalComponent
} from '../../task-queue/clients/packages/modals/request-transfer-sent-modal/request-transfer-sent-modal.component';
import {
  PackageCannotBeOpenModalComponent
} from '../modals/package-cannot-be-open-modal/package-cannot-be-open-modal.component';
import {
  CannotConvertPackageModalComponent
} from '../modals/cannot-convert-package-modal/cannot-convert-package-modal.component';
import {
  BenefitCategoryConflictModalComponent
} from '../modals/benefit-category-conflict-modal/benefit-category-conflict-modal.component';
import {RecipientModel} from '../models/recipient.model';
import {
  MembersOfBlockedOrOpenPackageModalComponent
} from '../modals/members-of-blocked-or-open-package-modal/members-of-blocked-or-open-package-modal.component';
import {
  ReminderInvitationRegisterModalComponent
} from '../modals/reminder-invitation-register-modal/reminder-invitation-register-modal.component';
import {
  ReminderApplicantPermissionModalComponent
} from '../modals/reminder-applicant-permission-modal/reminder-applicant-permission-modal.component';
import {PaymentFailedModalComponent} from '../modals/payment-failed-modal/payment-failed-modal.component';
import {NoResultsModalComponent} from '../modals/no-results-modal/no-results-modal.component';

export const activePackageId = 'ev-active-package-id';


@Injectable()
export class PackagesService {
  getApplicantRequest$: Observable<RequestState<PackageApplicant>>;
  getPackageRequest$: Observable<RequestState<Package>>;
  getEmailPostRequest$: Observable<RequestState<Email>>;
  getPackagesRequest$: Observable<RequestState<Package[]>>;
  getRetainerAgreementPostRequest$: Observable<RequestState<{ message: string }>>;
  getRetainerAgreementDeleteRequest$: Observable<RequestState<{ message: string }>>;
  activePackage$: Observable<Package>;
  activePackageId$: Observable<number>;
  package$: Observable<Package>;
  packages$: Observable<Package[]>;
  packageEntities$: Observable<Dictionary<Package>>;
  total$: Observable<number>;
  patchPackageRequest$: Observable<RequestState<Package>>;
  postPackageRequest$: Observable<RequestState<Package>>;
  postPackageWelcomeEmailRequest$: Observable<RequestState<{ message: string }>>;
  applicantInvitePostRequest$: Observable<RequestState<{ message: string }>>;
  changePackageStatusPostRequest$: Observable<RequestState<Package>>;
  deleteLeadPackagesRequest$: Observable<RequestState<Package[]>>;
  packagesTransferPostRequest$: Observable<RequestState<RecipientModel>>;
  packagesTransferByApplicantPostRequest$: Observable<RequestState<RecipientModel>>;
  feesBillPostRequest$: Observable<RequestState<any>>;
  addApplicantBtnClickedSubject$: Subject<boolean> = new Subject<boolean>();
  deleteSelectedLeadPackagesRequest$: Observable<RequestState<Package[]>>;
  deleteSelectedTransferredPackagesRequest$: Observable<RequestState<Package[]>>;
  changePackageOwedPatchRequest$: Observable<RequestState<Package>>;

  constructor(
    private store: Store<State>,
    private cookieService: CookieService,
    private ngbModal: NgbModal,
  ) {
    this.getApplicantRequest$ = this.store.pipe(select(getApplicantRequestState));
    this.getPackageRequest$ = this.store.pipe(select(getPackageRequestState));
    this.getEmailPostRequest$ = this.store.pipe(select(selectEmailPostRequestState));
    this.getPackagesRequest$ = this.store.pipe(select(selectPackagesGetRequestState));
    this.getRetainerAgreementPostRequest$ = this.store.pipe(select(getRetainerAgreementPostRequestState));
    this.getRetainerAgreementDeleteRequest$ = this.store.pipe(select(getRetainerAgreementDeleteRequestState));
    this.package$ = this.store.pipe(select(selectCurrentPackage));
    this.activePackage$ = this.store.pipe(select(getActivePackage));
    this.activePackageId$ = this.store.pipe(select(getActivePackageId));
    this.packages$ = this.store.pipe(select(getPackagesData));
    this.packageEntities$ = this.store.pipe(select(getPackagesEntities));
    this.patchPackageRequest$ = this.store.pipe(select(patchPackageRequestState));
    this.postPackageRequest$ = this.store.pipe(select(postPackageRequestState));
    this.postPackageWelcomeEmailRequest$ = this.store.pipe(select(postPackageWelcomeEmailRequestState));
    this.applicantInvitePostRequest$ = this.store.pipe(select(getApplicantInvitePostRequestState));
    this.changePackageStatusPostRequest$ = this.store.pipe(select(getChangePackageStatusPostRequestState));
    this.deleteLeadPackagesRequest$ = this.store.pipe(select(deleteLeadPackagesRequestState));
    this.packagesTransferPostRequest$ = this.store.pipe(select(selectTransferPostRequestState));
    this.packagesTransferByApplicantPostRequest$ = this.store.pipe(select(selectTransferByApplicantPostRequestState));
    this.feesBillPostRequest$ = this.store.pipe(select(getFeesBillPostRequestState));
    this.deleteSelectedLeadPackagesRequest$ = this.store.pipe(select(deleteSelectedLeadPackagesRequestState));
    this.deleteSelectedTransferredPackagesRequest$ = this.store.pipe(select(deleteSelectedTransferredPackagesRequestState));
    this.total$ = this.store.pipe(select(getPackagesTotal));
    this.changePackageOwedPatchRequest$ = this.store.pipe(select(getChangePackageOwedPatchRequestState));

  }

  getPackages(params?, isShowModal?, isEditPackagePage?) {
    if (params) {
      Object.keys(params).forEach((key) => (params[key] == null) && delete params[key]);
    }
    this.store.dispatch(new GetPackages({params, isShowModal, isEditPackagePage}));
    return this.getPackagesRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  getPackage(packageId) {
    this.store.dispatch(new GetPackage(packageId));
    return this.getPackageRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  selectPackage(packageId) {
    this.store.dispatch(new SelectPackageId(packageId));
  }

  removePackage() {
    this.store.dispatch(new RemovePackage());
  }

  removePackages() {
    this.store.dispatch(new RemovePackages());
  }

  removeLeadPackages(params) {
    this.store.dispatch(deleteLeadPackagesRequestHandler.requestAction(params));
    return this.deleteLeadPackagesRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  removeSelectedLeadPackages(params) {
    this.store.dispatch(deleteSelectedLeadPackagesRequestHandler.requestAction(params));
    return this.deleteSelectedLeadPackagesRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  removeSelectedTransferredPackages(params) {
    this.store.dispatch(deleteSelectedTransferredPackagesRequestHandler.requestAction(params));
    return this.deleteSelectedTransferredPackagesRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  getApplicant(searchParam) {
    this.store.dispatch(applicantGetRequestHandler.requestAction(searchParam));
    return this.getApplicantRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  sendPackageEmail(email) {
    this.store.dispatch(emailPostRequestHandler.requestAction(email));
    return this.getEmailPostRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  addRetainerAgreement(packageId, data) {
    this.store.dispatch(retainerAgreementPostRequestHandler.requestAction({id: packageId, file: data}));
    return this.getRetainerAgreementPostRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  removeRetainerAgreement(packageId) {
    this.store.dispatch(retainerAgreementDeleteRequestHandler.requestAction(packageId));
    return this.getRetainerAgreementDeleteRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  createPackage(data) {
    this.store.dispatch(new PostPackage(data));
    return this.postPackageRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  updatePackage(data) {
    this.store.dispatch(new PatchPackage(data));
    return this.patchPackageRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  updatePackageOwed(data: { id: number; owed: number }) {
    this.store.dispatch(new ChangePackageOwed(data));
    return this.changePackageOwedPatchRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  updatePackageWithoutReminder(data) {
    this.store.dispatch(new PatchPackageWithoutReminder(data));
    return this.patchPackageRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  reSendPackageEmail(packageId) {
    this.store.dispatch(packageWelcomeEmailPostRequestHandler.requestAction(packageId));
    return this.postPackageWelcomeEmailRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  resendWelcomeApplicant(packageId, applicantId) {
    const params = {
      packageId,
      applicantId,
    };
    this.store.dispatch(applicantInvitePostRequestHandler.requestAction(params));
    return this.applicantInvitePostRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  updatePackageStatus(data: { id: number; newStatus: PackageStatus }) {
    this.store.dispatch(new ChangePackageStatus(data));
    return this.changePackageStatusPostRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  updatePackageInState(data) {
    this.store.dispatch(new UpdatePackage(data));
  }

  setActivePackage(packageId: number) {
    this.store.dispatch(new SetActivePackageId(packageId));
  }

  clearActivePackage() {
    this.store.dispatch(new ClearActivePackage());
  }

  packagesTransfer(data) {
    this.store.dispatch(packagesTransferPostRequestHandler.requestAction(data));
    return this.packagesTransferPostRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  packagesTransferByApplicant(data) {
    this.store.dispatch(packagesTransferByApplicantPostRequestHandler.requestAction(data));
    return this.packagesTransferByApplicantPostRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  sendFeesBill(data) {
    this.store.dispatch(feesBillPostRequestHandler.requestAction(data));
    return this.feesBillPostRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  openBenefitCategoryConflictModal(content) {
    const modalRef = this.ngbModal.open(BenefitCategoryConflictModalComponent, {
      centered: true,
      size: 'lg',
    });
    modalRef.componentInstance.content = content;
    return fromPromise(modalRef.result).pipe(
      catchError(() => EMPTY),
    );
  }

  openTransferCaseRequestSentModal(request) {
    const modalRef = this.ngbModal.open(RequestTransferSentModalComponent, {
      centered: true,
    });
    modalRef.componentInstance.request = request;
    return fromPromise(modalRef.result).pipe(
      catchError(() => EMPTY),
    );
  }

  openPackageCannotBeOpenModal(pendingOptApplicants, denyOptApplicants) {
    const modalRef = this.ngbModal.open(PackageCannotBeOpenModalComponent, {
      centered: true,
    });
    modalRef.componentInstance.pendingOptApplicants = pendingOptApplicants;
    modalRef.componentInstance.denyOptApplicants = denyOptApplicants;
    return fromPromise(modalRef.result).pipe(
      catchError(() => EMPTY),
    );
  }

  openCannotConvertPackageModal(item) {
    const modalRef = this.ngbModal.open(CannotConvertPackageModalComponent, {
      centered: true,
      size: 'lg'
    });
    modalRef.componentInstance.item = item;
    return fromPromise(modalRef.result).pipe(
      catchError(() => EMPTY),
    );
  }

  openInBlockedOrOpenPackageModal() {
    const modalRef = this.ngbModal.open(MembersOfBlockedOrOpenPackageModalComponent, {
      centered: true,
      size: 'lg'
    });
    return fromPromise(modalRef.result).pipe(
      catchError(() => EMPTY),
    );
  }

  openReminderInvitationRegisterModal() {
    const modalRef = this.ngbModal.open(ReminderInvitationRegisterModalComponent, {
      centered: true,
      size: 'lg'
    });
    return fromPromise(modalRef.result).pipe(
      catchError(() => EMPTY),
    );
  }

  openReminderApplicantPermissionModal() {
    const modalRef = this.ngbModal.open(ReminderApplicantPermissionModalComponent, {
      centered: true,
      size: 'lg'
    });
    return fromPromise(modalRef.result).pipe(
      catchError(() => EMPTY),
    );
  }

  openPaymentFailedModal(errorContent) {
    const modalRef = this.ngbModal.open(PaymentFailedModalComponent, {
      centered: true,
      size: 'lg'
    });
    modalRef.componentInstance.errorContent = errorContent;
    return fromPromise(modalRef.result).pipe(
      catchError(() => EMPTY),
    );
  }

  noResultsModal(isEditPackagePage?) {
    const modalRef = this.ngbModal.open(NoResultsModalComponent, {
      centered: true,
      size: 'lg'
    });
    modalRef.componentInstance.noResultModalDescription = isEditPackagePage ? 'TEMPLATE.TASK_QUEUE.CLIENTS.NO_RESULTS_MODAL.PACKAGE_DESCRIPTION' : null;
    return fromPromise(modalRef.result).pipe(
      catchError(() => EMPTY),
    );
  }

  getActivePackageId() {
    return of(parseInt(this.cookieService.get(activePackageId), 10));
  }

  setActivePackageId(id) {
    this.cookieService.set(activePackageId, id, 1, '/', null, null, 'Strict');
  }

  removeActivePackageId() {
    this.cookieService.set(activePackageId, '', -1, '/', null, null, 'Strict');
  }
}
