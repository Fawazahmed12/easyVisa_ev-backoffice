import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { combineLatest, merge, Observable, Subject } from 'rxjs';
import {
  filter,
  map,
  pluck,
  shareReplay,
  startWith,
  withLatestFrom,
} from 'rxjs/operators';

import { SignUpService } from '../../../../../../../auth/services';
import { AsyncRequestValidator } from '../../../../../../../shared/validators/async-request.validator';

import { EditPreviewProfileService } from '../../../../edit-preview-profile.service';


@Component({
  selector: 'app-change-email',
  templateUrl: './change-email.component.html',
})

@DestroySubscribers()
export class ChangeEmailComponent implements OnInit, AddSubscribers, OnDestroy {
  profileEmail$: Observable<string>;
  sameEmails$: Observable<boolean>;
  resetSubject$: Subject<boolean> = new Subject();
  changeEmailSubject$: Subject<string> = new Subject();

  emailFormControl: FormControl;

  private subscribers: any = {};

  constructor(
    private editPreviewProfileService: EditPreviewProfileService,
    private signUpService: SignUpService,
  ) {
    this.createEmailFormControl();
  }

  get formValue$() {
    return this.emailFormControl.valueChanges.pipe(
      startWith(this.emailFormControl.value),
      shareReplay(1),
    );
  }

  ngOnInit() {
    this.profileEmail$ = this.editPreviewProfileService.profile$.pipe(
      filter(profile => !!profile),
      pluck('email')
    );

    this.sameEmails$ = combineLatest([
      this.profileEmail$,
      this.formValue$,
    ]).pipe(
      map(([profileEmail, emailFormValue]) => !!emailFormValue ? emailFormValue === profileEmail : true)
    );
  }

  addSubscribers() {
    this.subscribers.profileSubscription = merge(
      this.profileEmail$,
      this.resetSubject$
    ).pipe(
      withLatestFrom( this.profileEmail$))
    .subscribe(([, email]) => {
      this.emailFormControl.patchValue(email, {emitEvent: false});
      this.emailFormControl.setAsyncValidators( AsyncRequestValidator.createValidator(
        (value) => this.signUpService.emailValidateRequest(value),
        email,
      ));
    });

    this.subscribers.changeEmailSubscription = this.changeEmailSubject$.pipe(
      filter(email => !!email),
    ).subscribe((email) => this.editPreviewProfileService.updateProfileEmail(email));
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  createEmailFormControl(data = null) {
    this.emailFormControl = new FormControl(data,
      {
        updateOn: 'change',
        validators: [
          Validators.required,
          Validators.email
        ],
      });
  }

  resetEmail() {
    this.resetSubject$.next(true);
  }

  changeEmail() {
    this.changeEmailSubject$.next(this.emailFormControl.value);
  }
}
