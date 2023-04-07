import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';

import { EMPTY, Observable, Subject } from 'rxjs';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { catchError, filter, map, switchMap } from 'rxjs/operators';

import { Role } from '../../../core/models/role.enum';
import { UserService } from '../../../core/services';
import { rolesHasAccess } from '../../../shared/utils/roles-has-access';
import { RequestState } from '../../../core/ngrx/utils';
import { AlertsService } from '../../../core/services/alerts.service';

@Component({
  selector: 'app-create-alert',
  templateUrl: './create-alert.component.html',
})
@DestroySubscribers()
export class CreateAlertComponent implements OnInit, OnDestroy, AddSubscribers {
  sendAlertSubject$: Subject<boolean> = new Subject();
  getSendAlertPostRequest$: Observable<RequestState<any>>;
  isRoleOwner$: Observable<boolean>;
  formGroup: FormGroup;

  private subscribers: any = {};

  get subjectFormControl() {
    return this.formGroup.get('subject');
  }

  get sendToFormControl() {
    return this.formGroup.get('sendTo');
  }

  get sourceFormControl() {
    return this.formGroup.get('source');
  }

  get bodyFormControl() {
    return this.formGroup.get('body');
  }

  constructor(
    private alertsService: AlertsService,
    private userService: UserService,
  ) {
    this.createFormGroup();
  }

  ngOnInit() {
    this.getSendAlertPostRequest$ = this.alertsService.getSendAlertPostRequest$;
    this.isRoleOwner$ = this.userService.currentUser$.pipe(
      filter((user) => !!user),
      map((user) => rolesHasAccess(user.roles, [Role.ROLE_OWNER])
      )
    );
  }

  addSubscribers() {
    this.subscribers.sendAlertSubscription = this.sendAlertSubject$.pipe(
      filter(() => this.formGroup.valid),
      switchMap(() =>
        this.alertsService.sendAlert(this.formGroup.value).pipe(
          catchError((err) => EMPTY),
        ),
      ),
    ).subscribe(() => this.formGroup.reset({ sendTo: [] }));

    this.subscribers.disableFormSubscription = this.isRoleOwner$.pipe(
      filter((isRoleOwner) => !!isRoleOwner)
    ).subscribe((isRoleOwner) => {
      if (!isRoleOwner) {
        this.formGroup.disable();
      }
    });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  sendAlert() {
    this.sendAlertSubject$.next(true);
  }

  createFormGroup() {
    this.formGroup = new FormGroup({
      subject: new FormControl(null, Validators.required),
      sendTo: new FormControl([], this.hasSelectedCheckBox),
      source: new FormControl(null, Validators.required),
      body: new FormControl(null, Validators.required),
    });
  }

  hasSelectedCheckBox: ValidatorFn = (control: FormControl): ValidationErrors | null => control.value.length === 0 ? {required: false} : null;
}



