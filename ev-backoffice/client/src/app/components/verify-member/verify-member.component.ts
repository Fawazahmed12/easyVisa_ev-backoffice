import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { Observable, ReplaySubject, Subject } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { DestroySubscribers } from 'ngx-destroy-subscribers';

import { RequestState } from '../../core/ngrx/utils';
import { OrganizationService } from '../../core/services';

export interface WarningMessage {
  successMessage?: string;
  message?: string;
  firstName?: string;
  middleName?: string;
  lastName?: string;
}

export interface VerifyMember {
  easyVisaId: string;
  email: string;
}


@Component({
  selector: 'app-verify-member',
  templateUrl: './verify-member.component.html',
})

@DestroySubscribers()
export class VerifyMemberComponent implements OnInit {
  ValidateResponseSubject$: ReplaySubject<WarningMessage[]> = new ReplaySubject<WarningMessage[]>(1);

  @Input() labelEVID: string;
  @Input() labelEmail: string;
  @Input() labelButton: string;
  @Input('message')
  set message(value: any) {
    this.ValidateResponseSubject$.next(value);
  }
  @Output() invalidForm = new EventEmitter();
  @Output() verifyFormGroup = new EventEmitter();
  verifyAttorneyRequestState$: Observable<RequestState<{ representativeId: number }>>;
  private verifyMemberSubject$: Subject<VerifyMember> = new Subject<VerifyMember>();

  verifyMemberFormGroup: FormGroup;

  private subscribers: any = {};

  get emailFormControl() {
    return this.verifyMemberFormGroup.get('email');
  }

  get easyVisaIdFormControl() {
    return this.verifyMemberFormGroup.get('easyVisaId');
  }

  constructor(
    private organizationService: OrganizationService,
  ) {
  }

  ngOnInit() {
    this.verifyAttorneyRequestState$ = this.organizationService.verifyAttorneyRequestState$;

    this.verifyMemberFormGroup = new FormGroup({
      easyVisaId: new FormControl(null, [
        Validators.required,
        Validators.pattern('(^[A-Z]\\d{10}$)'),
      ]),
      email: new FormControl(null, [Validators.required, Validators.email]),
    });
  }

  addSubscribers() {
    this.subscribers.verifySubscription = this.verifyMemberSubject$.pipe(
      filter(() => this.verifyMemberFormGroup.valid),
      map(() => this.verifyMemberFormGroup.value),
    ).subscribe((data) => {
      this.verifyFormGroup.emit(data);
    });

    this.subscribers.verifyMemberSubscription = this.verifyMemberFormGroup.valueChanges.subscribe(
      () => {
        this.invalidForm.emit(null);
        this.ValidateResponseSubject$.next(null);
      });
  }

  verifyMember() {
    this.verifyMemberSubject$.next(this.verifyMemberFormGroup.value);
  }
}
