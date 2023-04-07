import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { catchError, filter, switchMap } from 'rxjs/operators';
import { EMPTY, Subject } from 'rxjs';

import { strengthPasswordValidator } from '../../../../../../../auth/validators/strength-password.validator';
import { compareValueValidator } from '../../../../../../../auth/validators/compare-value.validator';
import { AuthService } from '../../../../../../../core/services';


@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
})

@DestroySubscribers()
export class ChangePasswordComponent implements OnInit, AddSubscribers, OnDestroy {
  changePasswordSubject$: Subject<boolean> = new Subject();
  isSubmitted = false;
  formGroup: FormGroup;

  private subscribers: any = {};

  constructor(
    private authService: AuthService,
  ) {
    this.createFormGroup();
  }

  get newPasswordControl() {
    return this.formGroup.get('newPassword');
  }

  get oldPasswordControl() {
    return this.formGroup.get('oldPassword');
  }

  get repeatPasswordControl() {
    return this.formGroup.get('repeatPassword');
  }

  ngOnInit() {
    console.log(`${this.constructor.name} Initialized`);
  }

  addSubscribers() {
    this.subscribers.profileSubscription = this.changePasswordSubject$.pipe(
      filter(() => this.formGroup.valid),
      switchMap(() =>
        this.authService.changePassword({
          oldPassword: this.oldPasswordControl.value,
          newPassword: this.newPasswordControl.value,
        }).pipe(
          catchError(() => EMPTY),
        )
      )
    ).subscribe(() => {
      this.isSubmitted = false;
      this.formGroup.reset({
        oldPassword: '',
        newPassword: '',
        repeatPassword: ''
      });
    });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  createFormGroup() {
    this.formGroup = new FormGroup({
      oldPassword: new FormControl('', {
        updateOn: 'change',
        validators: [
          Validators.required,
        ]
      }),
      newPassword: new FormControl('', {
        updateOn: 'change',
        validators: [
          Validators.minLength(12),
          Validators.required,
          strengthPasswordValidator,
        ]
      }),
      repeatPassword: new FormControl('',
        [
          Validators.required,
        ],
      ),
    }, {
      updateOn: 'submit',
      validators: [
        compareValueValidator('newPassword', 'repeatPassword'),
      ],
    });
  }

  formSubmit() {
    this.isSubmitted = true;
    this.changePasswordSubject$.next(true);
  }
}
