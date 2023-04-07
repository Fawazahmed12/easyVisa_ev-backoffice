import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import { Observable, Subject, Subscription } from 'rxjs';
import { catchError, filter, map, switchMap, tap } from 'rxjs/operators';
import { of } from 'rxjs';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { RequestState } from '../../core/ngrx/utils';
import { ModalService } from '../../core/services';

import { strengthPasswordValidator } from '../validators/strength-password.validator';
import { ResetPasswordModel } from '../models/reset-password.model';

import { ResetPasswordService } from './reset-password.service';

export interface ResetPassword {
  password: string;
  token: string;
}

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss'],
})
@DestroySubscribers()
export class ResetPasswordComponent implements OnInit, OnDestroy, AddSubscribers {

  validatePassScore = 3;
  validateMinLength = 12;

  formGroup: FormGroup;

  token$: Observable<string>;
  resetPassword$: Observable<any>;
  resetPasswordSubject$: Subject<ResetPassword> = new Subject<ResetPassword>();
  resetPasswordRequest$: Observable<RequestState<ResetPasswordModel>> = this.resetPasswordService.resetPasswordRequest$;

  private resetPasswordSubscription: Subscription;
  private tokenSubscription: Subscription;
  private subscribers: any = {};

  constructor(
    public activatedRoute: ActivatedRoute,
    private resetPasswordService: ResetPasswordService,
    private router: Router,
    private modalService: ModalService,
  ) {
    this.createForm();
  }

  get passwordControl() {
    return this.formGroup.get('password');
  }

  ngOnInit() {
    this.token$ = this.activatedRoute.queryParams.pipe(
      map((params) => params.token)
    );

    this.resetPassword$ = this.resetPasswordSubject$.pipe(
      filter(() => this.formGroup.valid),
      switchMap((value) => this.resetPasswordService.resetPassword(value))
    );

  }

  addSubscribers() {
    this.subscribers.tokenSubscription = this.token$
    .subscribe((token) =>
      this.formGroup.patchValue({token})
    );
    this.subscribers.resetPasswordSubscription = this.resetPassword$.pipe(
      catchError((error: HttpErrorResponse) =>
        this.modalService.showErrorModal(error.error.errors).pipe(
          catchError((err) => of(err)),
          tap(() => this.router.navigate(['auth', 'login'])),
        )
      ),
    )
    .subscribe(() => {
      this.router.navigate(['auth', 'login']);
    });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  createForm() {
    this.formGroup = new FormGroup({
      password: new FormControl(
        '',
        [
          Validators.required,
          Validators.minLength(this.validateMinLength),
          strengthPasswordValidator,
        ],
      ),
      token: new FormControl('', Validators.required),
    });
  }

  formSubmit() {
    this.resetPasswordSubject$.next(this.formGroup.value);
  }

}
