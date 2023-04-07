import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { catchError, filter, map, switchMap } from 'rxjs/operators';
import { fromPromise } from 'rxjs/internal-compatibility';
import { of } from 'rxjs';
import { Observable } from 'rxjs';

import { ModalService } from '../../core/services/modal.service';

import { SignUpService } from '../services';

@Component({
  selector: 'app-verify-registration',
  templateUrl: './verify-registration.component.html',
})
@DestroySubscribers()
export class VerifyRegistrationComponent implements AddSubscribers, OnDestroy, OnInit {

  private token$: Observable<string>;

  private subscribers: any = {};

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private signUpService: SignUpService,
    private modalService: ModalService,
  ) {
  }

  ngOnInit() {
    this.token$ = this.activatedRoute.queryParams.pipe(
      map((params) => params[ 'token' ])
    );
  }

  addSubscribers() {
    this.subscribers.verifyAttorneySubscription = this.token$.pipe(
      switchMap((token) =>
        this.signUpService.verifyAttorney(token).pipe(
          catchError((error: HttpErrorResponse) =>
            this.modalService.showErrorModal(error.error.errors).pipe(
              catchError((err) => of(err)),
              switchMap(() => fromPromise(this.router.navigate(['auth', 'attorney-sign-up']))),
              filter(() => false),
            )
          ),
        )
      ),
    )
    .subscribe(() => {
      this.router.navigate([ 'auth', 'login' ]);
    });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

}
