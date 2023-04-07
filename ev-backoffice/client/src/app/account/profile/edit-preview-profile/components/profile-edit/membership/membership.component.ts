import { Component, OnInit, ViewChild } from '@angular/core';

import { EMPTY, Observable, Subject } from 'rxjs';
import { catchError, filter, map, switchMap, withLatestFrom } from 'rxjs/operators';
import { fromPromise } from 'rxjs/internal-compatibility';
import { DestroySubscribers } from 'ngx-destroy-subscribers';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ConfirmButtonType } from '../../../../../../core/modals/confirm-modal/confirm-modal.component';
import { OrganizationType } from '../../../../../../core/models/organization-type.enum';
import { Organization } from '../../../../../../core/models/organization.model';
import { ModalService, OrganizationService, UserService } from '../../../../../../core/services';
import { Profile } from '../../../../../../core/models/profile.model';
import { AttorneyMenu } from '../../../../../../core/models/attorney.model';
import { ImportantMessageModalComponent } from './important-message-modal/important-message-modal.component';
import { User } from '../../../../../../core/models/user.model';
import { Role } from '../../../../../../core/models/role.enum';
import { InviteRequestService } from '../../../../../services/invite-request.service';
import { Invite } from '../../../../../models/invite.model';
import { EmployeePosition } from '../../../../../permissions/models/employee-position.enum';
import { RequestJoin } from '../../../../../models/request-join.model';

import { AttorneyProfile } from '../../../models/attorney-profile.model';
import { EditPreviewProfileService } from '../../../edit-preview-profile.service';
import { OrganizationProfile } from '../../../models/organization-profile.model';

import { MembershipService } from './membership.service';

@Component({
  selector: 'app-membership',
  templateUrl: './membership.component.html',
})
@DestroySubscribers()
export class MembershipComponent implements OnInit {
  @ViewChild('mandatoryAdmin', { static: true }) mandatoryAdmin;
  @ViewChild('leaveRecognizedOrganization', { static: true }) leaveRecognizedOrganization;
  @ViewChild('lawPracticeWillBeDeleted', { static: true }) lawPracticeWillBeDeleted;
  @ViewChild('leaveLegalPracticeModal', { static: true }) leaveLegalPracticeModal;
  @ViewChild('employeeLeaveLegalPractice', { static: true }) employeeLeaveLegalPractice;

  activeOrganization$: Observable<Organization>;
  activeOrganizationId$: Observable<string>;
  practiceType$: Observable<string>;
  profile$: Observable<AttorneyProfile | Profile>;
  organization$: Observable<OrganizationProfile>;
  openModal$: Observable<NgbModal>;
  currentUser$: Observable<User>;
  currentUserId$: Observable<string>;
  isEmployee$: Observable<boolean>;
  isSoloPractitioner$: Observable<boolean>;
  invite$: Observable<Invite>;
  requestJoin$: Observable<RequestJoin>;
  representativesMenu$: Observable<AttorneyMenu[]>;
  currentPosition$: Observable<EmployeePosition>;

  leaveLegalPractice$ = new Subject<any>();
  deleteInvitationSubject$ = new Subject<any>();
  deleteRequestSubject$ = new Subject<any>();

  lawFirm = OrganizationType.LAW_FIRM;
  EmployeePosition = EmployeePosition;

  nonAttorneyPositions = [
    EmployeePosition.EMPLOYEE,
    EmployeePosition.TRAINEE,
    EmployeePosition.MANAGER,
  ];

  private subscribers: any = {};

  get hasNonAttorneyPosition$() {
    return this.currentPosition$.pipe(
      map(position => position && this.nonAttorneyPositions.some(nonAttorneyPosition => nonAttorneyPosition === position))
    );
  }

  constructor(
    private profileService: EditPreviewProfileService,
    private organizationService: OrganizationService,
    private ngbModal: NgbModal,
    private modalService: ModalService,
    private membershipService: MembershipService,
    private userService: UserService,
    private inviteRequestService: InviteRequestService,
  ) {

  }

  ngOnInit() {
    this.invite$ = this.inviteRequestService.invite$;
    this.requestJoin$ = this.inviteRequestService.requestJoin$;
    this.activeOrganization$ = this.organizationService.activeOrganization$;
    this.activeOrganizationId$ = this.organizationService.activeOrganizationId$;
    this.profile$ = this.profileService.profile$;
    this.organization$ = this.profileService.organization$;
    this.currentUser$ = this.userService.currentUser$;
    this.currentUserId$ = this.userService.currentUser$.pipe(
      filter((currentUser) => !!currentUser),
      map((currentUser) => currentUser.id)
    );
    this.isEmployee$ = this.userService.hasAccess([Role.ROLE_EMPLOYEE]);

    this.isSoloPractitioner$ = this.activeOrganization$.pipe(
      filter((res) => !!res),
      map((organization) => organization.organizationType === OrganizationType.SOLO_PRACTICE)
    );

    this.practiceType$ = this.activeOrganization$.pipe(
      filter((activeOrganization) => !!activeOrganization),
      map((activeOrganization) => {
        switch (activeOrganization.organizationType) {
          case OrganizationType.SOLO_PRACTICE: {
            return 'TEMPLATE.ORGANIZATION_TYPES.SOLO_PRACTICE';
          }
          case OrganizationType.LAW_FIRM: {
            return 'TEMPLATE.ORGANIZATION_TYPES.MEMBER_OF_LAW_FIRM';
          }
          default: {
            return '';
          }
        }
      })
    );

    this.representativesMenu$ = this.organizationService.representativesMenu$;
    this.currentPosition$ = this.organizationService.currentPosition$;
  }

  addSubscribers() {
    this.subscribers.inviteMemberSubscription = this.leaveLegalPractice$.pipe(
      withLatestFrom(
        this.activeOrganization$,
        this.representativesMenu$,
        this.organizationService.currentPosition$,
      ),
      filter(([, activeOrganization, representatives, ]) => !!activeOrganization && !!representatives),
      switchMap(([, activeOrganization, representatives, position]) =>
        this.openModal(activeOrganization, representatives, position)
      ),
      withLatestFrom(
        this.activeOrganizationId$,
        this.currentUserId$
      )
    ).subscribe(([, activeOrganizationId, currentUserId]) =>
      this.membershipService.leaveOrganization(
        {
          employeeId: currentUserId,
          organizationId: activeOrganizationId
        }
      )
    );

    this.subscribers.deleteInvitationSubscription = this.deleteInvitationSubject$
    .subscribe(() => this.inviteRequestService.deleteInvite());

    this.subscribers.deleteInvitationSubscription = this.deleteRequestSubject$
    .subscribe(() => this.inviteRequestService.deleteRequest());
  }

  openModal(activeOrganization, representatives, position) {

    if (activeOrganization.organizationType === OrganizationType.RECOGNIZED_ORGANIZATION) {
      this.openModal$ = this.openLeaveRecognizedOrganization();
    } else if (this.nonAttorneyPositions.some(nonAttorneyPosition => nonAttorneyPosition === position)) {
      this.openModal$ = this.openEmployeeLeaveLegalPractice();
    } else if (representatives.length && representatives.length === 2) {
      this.openModal$ = this.openLawFirmWillBeDeleted();
    } else {
      this.openModal$ = this.openLeaveLegalPractice();
    }
    return this.openModal$;
  }

  openImportantMessageModal() {
    const modalRef = this.ngbModal.open(ImportantMessageModalComponent, {
      windowClass: 'custom-modal-lg',
      centered: true,
    });
    return fromPromise(modalRef.result);
  }

  openLeaveRecognizedOrganization() {
    const buttons = [
      {
        label: 'FORM.BUTTON.CANCEL',
        type: ConfirmButtonType.Dismiss,
        className: 'btn btn-primary mr-2 min-w-100',
      },
      {
        label: 'FORM.BUTTON.SUBMIT',
        type: ConfirmButtonType.Close,
        className: 'btn btn-primary mr-2 min-w-100',
      },
    ];

    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.ACCOUNT.PROFILE.MODALS.LEAVE_THIS_RECOGNIZED_ORGANIZATION.HEADER',
      body: this.leaveRecognizedOrganization,
      buttons,
      centered: true,
    }).pipe(
      catchError(() => EMPTY)
    );
  }

  openLawFirmWillBeDeleted() {
    const buttons = [
      {
        label: 'FORM.BUTTON.CANCEL',
        type: ConfirmButtonType.Dismiss,
        className: 'btn btn-primary mr-2 min-w-100',
      },
      {
        label: 'TEMPLATE.ACCOUNT.PROFILE.MODALS.LAW_PRACTICE_ACCOUNT_WILL_BE_DELETED.DELETE_BUTTON',
        type: ConfirmButtonType.Close,
        className: 'btn btn-primary mr-2 min-w-100',
      },
    ];

    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.ACCOUNT.PROFILE.MODALS.LAW_PRACTICE_ACCOUNT_WILL_BE_DELETED.HEADER',
      body: this.lawPracticeWillBeDeleted,
      buttons,
      size: 'lg',
      centered: true,
    }).pipe(
      catchError(() => EMPTY)
    );
  }

  openLeaveLegalPractice() {
    const buttons = [
      {
        label: 'FORM.BUTTON.CANCEL',
        type: ConfirmButtonType.Dismiss,
        className: 'btn btn-primary mr-2 min-w-100',
      },
      {
        label: 'FORM.BUTTON.OK',
        type: ConfirmButtonType.Close,
        className: 'btn btn-primary mr-2 min-w-100',
      },
    ];

    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.ACCOUNT.PROFILE.MODALS.LEAVE_LEGAL_PRACTICE.HEADER',
      body: this.leaveLegalPracticeModal,
      buttons,
      size: 'lg',
      centered: true,
    }).pipe(
      catchError(() => EMPTY)
    );
  }

  openEmployeeLeaveLegalPractice() {
    const buttons = [
      {
        label: 'FORM.BUTTON.CANCEL',
        type: ConfirmButtonType.Dismiss,
        className: 'btn btn-primary mr-2 min-w-100',
      },
      {
        label: 'FORM.BUTTON.OK',
        type: ConfirmButtonType.Close,
        className: 'btn btn-primary mr-2 min-w-100',
      },
    ];

    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.ACCOUNT.PROFILE.MODALS.EMPLOYEE_LEAVE_LEGAL_PRACTICE.HEADER',
      body: this.employeeLeaveLegalPractice,
      buttons,
      size: 'lg',
      centered: true,
    }).pipe(
      catchError(() => EMPTY)
    );
  }

  leaveLegalPractice() {
    this.leaveLegalPractice$.next(true);
  }

  deleteInvitation() {
    this.deleteInvitationSubject$.next(true);
  }

  deleteRequestJoin() {
    this.deleteRequestSubject$.next(true);
  }
}
