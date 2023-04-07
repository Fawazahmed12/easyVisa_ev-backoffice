import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

import { EMPTY, Observable, ReplaySubject, Subject } from 'rxjs';
import { catchError, filter, switchMap, withLatestFrom } from 'rxjs/operators';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ModalService, OrganizationService, UserService } from '../../../core/services';
import { Organization } from '../../../core/models/organization.model';

import { PermissionsService } from '../permissions.service';


@Component({
  selector: 'app-invite-member',
  templateUrl: './invite-member.component.html',
})

@DestroySubscribers()
export class InviteMemberComponent implements OnInit, AddSubscribers {
  activeOrganization$: Observable<Organization>;
  inviteMemberSubject$: Subject<boolean> = new Subject<boolean>();
  showWarningSubject$: ReplaySubject<boolean> = new ReplaySubject<boolean>(1);
  messageSubject$: ReplaySubject<string> = new ReplaySubject<string>(1);

  formGroup: FormGroup;

  private subscribers: any = {};

  get emailFormControl() {
    return this.formGroup.get('email');
  }

  get easyVisaIdFormControl() {
    return this.formGroup.get('evId');
  }

  constructor(
    private userService: UserService,
    private organizationService: OrganizationService,
    private modalService: ModalService,
    private permissionsService: PermissionsService,
    private ngbModal: NgbModal,
    private router: Router,
  ) {
    this.createFormGroup();
  }

  ngOnInit() {
    this.activeOrganization$ = this.organizationService.activeOrganization$;
  }

  addSubscribers() {
    this.subscribers.inviteMemberLawFirmSubscription = this.inviteMemberSubject$.pipe(
      filter(() => this.formGroup.valid),
      withLatestFrom(this.organizationService.activeOrganizationId$),
      switchMap(([data, activeOrganizationId]) =>
        this.permissionsService.inviteMember({
          formGroup: data,
          activeOrganizationId
        }).pipe(
          catchError((error: HttpErrorResponse) => {
              if (error.status !== 401) {
                this.messageSubject$.next(error.error.errors[0].message);
              }
              return EMPTY;
            }
          ),
        ))
    ).subscribe(() => {
      this.router.navigate(['/account/permissions']);
    });

    this.subscribers.formGroupSubscription = this.formGroup.valueChanges.subscribe(
      () => {
        this.messageSubject$.next(null);
        this.showWarningSubject$.next(null);
      });
  }

  createFormGroup() {
    this.formGroup = new FormGroup({
        evId: new FormControl(null, {
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

  sendInvite() {
    this.showWarningSubject$.next(true);
    return this.inviteMemberSubject$.next(this.formGroup.value);
  }
}
