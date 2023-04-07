import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { EMPTY, Observable, ReplaySubject, Subject } from 'rxjs';
import {
  catchError,
  distinctUntilChanged,
  filter,
  map,
  shareReplay,
  startWith,
  switchMap,
} from 'rxjs/operators';

import { isEqual } from 'lodash-es';

import { ModalService } from '../../../../../../../core/services';
import { Review } from '../../../../../../models/review.model';
import { ReviewService } from '../../../../../../services/review.service';
import { ReplyCheck } from '../../../../../../models/reply-check.model';


@Component({
  selector: 'app-write-reply',
  templateUrl: './write-reply.component.html',
})
@DestroySubscribers()
export class WriteReplyComponent implements OnInit, AddSubscribers {

  activeReview$: Observable<Review>;
  addReplySubject$: Subject<any> = new Subject<any>();
  closeModalSubject$: Subject<any> = new Subject<any>();
  charactersCountSubject$: ReplaySubject<number> = new ReplaySubject<number>(1);
  isCancelButtonDisabled$: Observable<boolean>;
  maxLength$: Observable<number>;

  formGroup: FormGroup;

  private subscribers: any = {};

  get replyFormControl() {
    return this.formGroup.get('reply');
  }

  constructor(
    private reviewService: ReviewService,
    private modalService: ModalService,
  ) {
    this.createFormGroup();
  }

  get formValue$() {
    return this.formGroup.valueChanges.pipe(
      startWith(this.formGroup.value),
      shareReplay(1)
    );
  }

  ngOnInit() {
    this.activeReview$ = this.reviewService.activeReview$;

    this.isCancelButtonDisabled$ = this.activeReview$.pipe(
      switchMap((activeReview) =>
        this.formValue$.pipe(
          map((formValue) => [activeReview, formValue])
        )
      ),
      map(([activeReview, formValue]) => isEqual(new ReplyCheck(activeReview), new ReplyCheck(formValue))),
      shareReplay(1),
    );

    this.maxLength$ = this.replyFormControl.valueChanges.pipe(
      filter((res) => !!res),
      map((review) => {
          const maxLengthWithoutSpace = 1000;
          return maxLengthWithoutSpace + (review.length - review.replace(/\s+/g, '').length);
        }
      ));
  }

  addSubscribers() {
    this.subscribers.activeReviewSubscription = this.activeReview$.pipe(
      filter((activeReview) => !!activeReview),
    ).subscribe((activeReview) => this.formGroup.patchValue(activeReview));

    this.subscribers.reviewFormControlSubscription = this.replyFormControl.valueChanges.pipe(
      filter((data) => !!data),
      map((data) => this.countCharacters(data)),
      distinctUntilChanged(),
    ).subscribe((data) => this.charactersCountSubject$.next(data));

    this.subscribers.addReplySubjectSubscription = this.addReplySubject$.pipe(
      filter(() => this.formGroup.valid),
      switchMap(() => this.reviewService.patchReview(this.formGroup.getRawValue()).pipe(
            catchError((error: HttpErrorResponse) => {
                if (error.status !== 401) {
                  this.modalService.showErrorModal(error.error.errors || [error.error]);
                }
                return EMPTY;
              }
            ),
          ))
    ).subscribe(() => this.closeModal());

    this.subscribers.addReplySubjectSubscription = this.closeModalSubject$.subscribe(
      () => this.modalService.closeAllModals()
    );
  }

  formSubmit() {
    this.addReplySubject$.next(true);
  }

  closeModal() {
    this.closeModalSubject$.next();
  }

  createFormGroup(data?) {
    this.formGroup = new FormGroup({
      id: new FormControl(data ? data.id : null),
      reply: new FormControl(data ? data.review : null,
        [
          Validators.required,
          this.maxWordsValidation()
        ]),
    });
  }

  maxWordsValidation(): ValidatorFn {
    return ({value}: FormControl): ValidationErrors | null => {
      const charactersCounterValue = value ? this.countCharacters(value) : 0;
      return charactersCounterValue < 0 ? {invalidLength: true} : null;
    };
  }

  countCharacters(data) {
    let result: number;
    const maxCharacters = 1000;
    result = data !== '' ? data.replace(/\s+/g, '').length : 0;
    return result ? maxCharacters - result : maxCharacters;
  }
}
