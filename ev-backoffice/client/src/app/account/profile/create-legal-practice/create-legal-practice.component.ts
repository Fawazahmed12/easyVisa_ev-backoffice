import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { EMPTY, Observable, of, ReplaySubject, Subject } from 'rxjs';
import { catchError, delay, filter, switchMap } from 'rxjs/operators';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { fromPromise } from 'rxjs/internal-compatibility';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { Organization } from '../../../core/models/organization.model';
import { ModalService, OrganizationService, UserService } from '../../../core/services';

import { InviteRequestService } from '../../services/invite-request.service';
import { InvitationRequestSentComponent } from '../../modals/invitation-request-sent/invitation-request-sent.component';


@Component({
  selector: 'app-create-legal-practice',
  templateUrl: './create-legal-practice.component.html',
  styleUrls: ['./create-legal-practice.component.scss'],
})
@DestroySubscribers()
export class CreateLegalPracticeComponent implements OnInit, AddSubscribers {
  activeOrganization$: Observable<Organization>;
  inviteAttorneySubject$: Subject<boolean> = new Subject<boolean>();
  showWarningSubject$: ReplaySubject<boolean> = new ReplaySubject<boolean>(1);
  messageSubject$: ReplaySubject<string> = new ReplaySubject<string>(1);

  formGroup: FormGroup;

  private subscribers: any = {};

  get emailFormControl() {
    return this.formGroup.get('email');
  }

  get easyVisaIdFormControl() {
    return this.formGroup.get('easyVisaId');
  }

  constructor(
    private userService: UserService,
    private modalService: ModalService,
    private organizationService: OrganizationService,
    private inviteRequestService: InviteRequestService,
    private ngbModal: NgbModal,
    private router: Router,
  ) {
    this.createFormGroup();
  }

  ngOnInit() {
    this.activeOrganization$ = this.organizationService.activeOrganization$;
  }

  addSubscribers() {
    this.subscribers.inviteMemberLawFirmSubscription = this.inviteAttorneySubject$.pipe(
      delay(500),
      filter(() => this.formGroup.valid),
      switchMap((data) =>
        this.inviteRequestService.putInvite(data).pipe(
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
            Validators.pattern('(^[A-Z]\\d{10}$)'),
          ]
        }),
        email: new FormControl(null, {
          validators: [
            Validators.required,
            Validators.email
          ]
        }),
      },
    );
  }

  openInviteSentModal() {
    const modalRef = this.ngbModal.open(InvitationRequestSentComponent, {
      centered: true,
      size: 'lg'
    });
    modalRef.componentInstance.isRequest = false;
    return fromPromise(modalRef.result);
  }

  sendInvite() {
    this.showWarningSubject$.next(true);
    return this.inviteAttorneySubject$.next(this.formGroup.value);
  }
}
