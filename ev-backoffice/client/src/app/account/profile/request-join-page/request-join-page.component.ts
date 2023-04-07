import { HttpErrorResponse } from '@angular/common/http';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { EMPTY, Observable, of, ReplaySubject, Subject } from 'rxjs';
import { catchError, delay, filter, switchMap } from 'rxjs/operators';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { fromPromise } from 'rxjs/internal-compatibility';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { RequestState } from '../../../core/ngrx/utils';
import { Organization } from '../../../core/models/organization.model';
import { Role } from '../../../core/models/role.enum';
import { ModalService, OrganizationService, UserService } from '../../../core/services';

import { Invite } from '../../models/invite.model';
import { InviteRequestService } from '../../services/invite-request.service';
import { InvitationRequestSentComponent } from '../../modals/invitation-request-sent/invitation-request-sent.component';

import { RequestJoinPageService } from './request-join-page.service';


@Component({
  selector: 'app-create-legal-practice',
  templateUrl: './request-join-page.component.html',
})
@DestroySubscribers()
export class RequestJoinPageComponent implements OnInit, AddSubscribers {
  activeOrganization$: Observable<Organization>;
  inviteAttorneySubject$: Subject<boolean> = new Subject<boolean>();
  showWarningSubject$: ReplaySubject<boolean> = new ReplaySubject<boolean>(1);
  messageSubject$: ReplaySubject<string> = new ReplaySubject<string>(1);
  requestPutState$: Observable<RequestState<Invite>>;
  isAttorney$: Observable<boolean>;
  isEmployee$: Observable<boolean>;

  formGroup: FormGroup;

  private subscribers: any = {};

  constructor(
    private inviteRequestService: InviteRequestService,
    private userService: UserService,
    private organizationService: OrganizationService,
    private requestJoinPageService: RequestJoinPageService,
    private modalService: ModalService,
    private ngbModal: NgbModal,
    private router: Router,
  ) {
    this.createFormGroup();
  }

  get emailFormControl() {
    return this.formGroup.get('email');
  }

  get easyVisaIdFormControl() {
    return this.formGroup.get('easyVisaId');
  }

  ngOnInit() {
    this.requestPutState$ = this.inviteRequestService.requestPutState$;
    this.activeOrganization$ = this.organizationService.activeOrganization$;
    this.isAttorney$ = this.userService.hasAccess([Role.ROLE_ATTORNEY]);
    this.isEmployee$ = this.userService.hasAccess([Role.ROLE_EMPLOYEE]);
  }

  addSubscribers() {
    this.subscribers.inviteMemberLawFirmSubscription = this.inviteAttorneySubject$.pipe(
      delay(500),
      filter(() => this.formGroup.valid),
      switchMap((data) =>
        this.inviteRequestService.putRequest(data).pipe(
          catchError((error: HttpErrorResponse) => {
              if (error.status !== 401) {
                this.messageSubject$.next(error.error.errors[0].message);
              }
              return EMPTY;
            }
          ),
        )),
      switchMap(() => this.openInviteSentModal().pipe(
        catchError(() => of(true))
      )),
    ).subscribe(() => {
      this.router.navigate(['/account/profile']);
    });

    this.subscribers.formGroupSubscription = this.formGroup.valueChanges.subscribe(
      () => {
        this.messageSubject$.next(null);
        this.showWarningSubject$.next(null);
      });
  }

  createFormGroup() {
    this.formGroup = new FormGroup({
        easyVisaId: new FormControl(null, {
          validators: [
            Validators.required,
            Validators.pattern(/(^(ORG|L)\d{10}$)/),
          ]
        }),
        email: new FormControl(null, {
          validators: [
            Validators.required,
            Validators.email
          ]
        }),
      });
  }

  openInviteSentModal() {
    const modalRef = this.ngbModal.open(InvitationRequestSentComponent, {
      centered: true,
      size: 'lg'
    });
    modalRef.componentInstance.isRequest = true;
    return fromPromise(modalRef.result);
  }

  sendRequest() {
    this.showWarningSubject$.next(true);
    return this.inviteAttorneySubject$.next(this.formGroup.value);
  }
}
