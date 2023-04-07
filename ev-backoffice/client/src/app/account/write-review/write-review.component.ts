import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { Dictionary } from '@ngrx/entity';

import { combineLatest, EMPTY, NEVER, Observable, ReplaySubject, Subject } from 'rxjs';
import {
  catchError,
  distinctUntilChanged,
  filter,
  map,
  shareReplay,
  startWith,
  switchMap,
  withLatestFrom,
} from 'rxjs/operators';
import { isEqual, uniqBy } from 'lodash-es';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { ModalService, PackagesService, UserService } from '../../core/services';
import { Package } from '../../core/models/package/package.model';
import { ConfirmButtonType, OkButtonLg } from '../../core/modals/confirm-modal/confirm-modal.component';
import { OrganizationType } from '../../core/models/organization-type.enum';
import { RequestState } from '../../core/ngrx/utils';
import { Attorney } from '../../core/models/attorney.model';

import { Review } from '../models/review.model';
import { ReviewCheck } from '../models/review-check.model';
import { ReviewService } from '../services/review.service';
import { states } from '../../core/models/states';
import { countries } from '../../core/models/countries';
import { benefitCategories } from '../../core/models/benefit-categories';


@Component({
  selector: 'app-write-review',
  templateUrl: './write-review.component.html',
  styleUrls: [ './write-review.component.scss' ]
})
@DestroySubscribers()
export class WriteReviewComponent implements OnInit, AddSubscribers, OnDestroy {
  @ViewChild('writingTips', { static: true }) writingTips;
  @ViewChild('selectRepresentative', { static: true }) selectRepresentative;

  packageEntities$: Observable<Dictionary<Package>>;
  package$: Observable<Package>;
  activeReview$: Observable<Review>;
  maxLength$: Observable<number>;
  reviewGetState$: Observable<RequestState<Review>>;
  isSubmitButtonDisabledSubject$: ReplaySubject<boolean> = new ReplaySubject<boolean>(1);
  charactersCount$: Observable<number>;
  selectedRepresentativeTypeSubject$: ReplaySubject<OrganizationType> = new ReplaySubject<OrganizationType>(1);
  createReviewSubject$: Subject<any> = new Subject<any>();
  representativeAssigned$: Observable<Attorney[]>;
  benefitCategory$: Observable<string>;

  currentAssignee: Attorney;
  formGroup: FormGroup;

  states = states;
  countries = countries;
  benefitCategories = benefitCategories;

  private subscribers: any = {};

  constructor(
    private userService: UserService,
    private packagesService: PackagesService,
    private reviewService: ReviewService,
    private modalService: ModalService,
  ) {
    this.createFormGroup();
  }

  get packageIdFormControl() {
    return this.formGroup.get('packageId');
  }

  get representativeIdFormControl() {
    return this.formGroup.get('representativeId');
  }

  get ratingFormControl() {
    return this.formGroup.get('rating');
  }

  get titleFormControl() {
    return this.formGroup.get('title');
  }

  get reviewFormControl() {
    return this.formGroup.get('review');
  }

  get representativeFormControl$() {
    return this.representativeIdFormControl.valueChanges.pipe(
      startWith(this.representativeIdFormControl.value),
    );
  }

  get packageIdFormControl$() {
    return this.packageIdFormControl.valueChanges.pipe(
      startWith(this.packageIdFormControl.value),
    );
  }

  ngOnInit() {
    this.setActiveReview(null);
    this.reviewGetState$ = this.reviewService.reviewGetState$;
    this.activeReview$ = this.reviewService.activeReview$;
    this.packageEntities$ = this.packagesService.packageEntities$;
    this.package$ = this.packagesService.package$.pipe(
      filter((item) => !!item),
    );

    this.maxLength$ = this.reviewFormControl.valueChanges.pipe(
      filter((res) => !!res),
      map((review) => {
          const maxLengthWithoutSpace = 1000;
          return maxLengthWithoutSpace + (review.length - review.replace(/\s+/g, '').length);
        }
      ));

    this.representativeAssigned$ = this.package$.pipe(
      filter(item => !!item),
      map(item => item.assignees),
      map((assignees) => uniqBy(assignees, 'id')),
    );

    this.charactersCount$ = this.reviewFormControl.valueChanges.pipe(
      filter((data) => !!data),
      map((data) => this.countCharacters(data)),
      distinctUntilChanged(),
      shareReplay(1),
    );

    this.benefitCategory$ = this.package$.pipe(
      filter(item => !!item),
      map(packages => packages.applicants.reduce((acc, applicant) => applicant.benefitCategory || acc, ''))
    );
  }

  addSubscribers() {
    this.subscribers.formGroupSubscription = this.formGroup.valueChanges.pipe(
      withLatestFrom(this.activeReview$),
      map(([ activeReview, formValue ]) => isEqual(new ReviewCheck(activeReview), new ReviewCheck(formValue))),
    ).subscribe((res) => this.isSubmitButtonDisabledSubject$.next(res));

    this.subscribers.packegeIdFormControlSubscription = this.packageIdFormControl.valueChanges
      .subscribe((id) => {
          this.representativeIdFormControl.patchValue(null);
          this.ratingFormControl.patchValue(null);
          this.titleFormControl.patchValue(null);
          this.reviewFormControl.patchValue(null);
          this.packagesService.selectPackage(id);
        }
      );

    this.subscribers.packageSubscription = this.packageIdFormControl.valueChanges.pipe(
      filter((id) => !!id),
      withLatestFrom(this.packageEntities$),
      map(([ itemId, items ]) => items[ itemId ]),
      map((items) => {
          if (items.assignees.length > 1) {
            this.openSelectRepresentative();
          }
          if (items.assignees.length) {
            const assigneeId = items.assignees.find((item) => !!item.id).id;
            return [ assigneeId, items.assignees.length ];
          }
        }
      )
    ).subscribe(([ assigneeId, assigneesCount ]) => {
      if (assigneesCount === 1) {
        this.representativeIdFormControl.patchValue(assigneeId);
        this.representativeIdFormControl.disable({ emitEvent: false });
      } else {
        this.representativeIdFormControl.enable({ emitEvent: false });
      }
    });

    this.subscribers.packageSubscription = combineLatest([
      this.representativeFormControl$,
      this.packageIdFormControl$,
    ]).pipe(
      filter(([ repId, packageId ]) => !!repId && !!packageId),
    ).subscribe(([ representativeId, packageId ]) => {
      this.ratingFormControl.patchValue(null);
      this.titleFormControl.patchValue(null);
      this.reviewFormControl.patchValue(null);
      this.reviewService.getReview(
        {
          packageId,
          representativeId
        });
    });

    this.subscribers.packageSubscription = this.activeReview$.pipe(
      filter((activeReview) => !!activeReview),
    ).subscribe((activeReview) => this.formGroup.patchValue(activeReview, { emitEvent: false }));

    this.subscribers.createReviewSubscription = this.createReviewSubject$.pipe(
      filter(() => this.formGroup.valid),
      withLatestFrom(this.activeReview$),
      switchMap(([ value, activeReview ]) =>
        activeReview ? this.reviewService.updateReview(value) : this.reviewService.createReview(value)
      ),
      catchError(() => NEVER),
    ).subscribe(() => this.isSubmitButtonDisabledSubject$.next(true));

    this.subscribers.representativeIdFormControlSubscription = this.representativeIdFormControl.valueChanges.pipe(
      withLatestFrom(this.package$),
      map(([ repId, item ]) => repId ? item.assignees.find((assignee) => assignee.id === +repId) : null),
    ).subscribe((data) => {
      this.currentAssignee = data;
    });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  createFormGroup(data?) {
    this.formGroup = new FormGroup({
      id: new FormControl(data ? data.id : null),
      packageId: new FormControl(data ? data.packageId : null),
      representativeId: new FormControl(data ? data.representativeId : null, Validators.required),
      rating: new FormControl(data ? data.rating : null, Validators.required),
      title: new FormControl(data ? data.title : null, Validators.required),
      review: new FormControl(data ? data.review : null,
        [
          Validators.required,
          this.maxWordsValidation()
        ]),
    });
  }

  countCharacters(data) {
    let result: number;
    const maxCharacters = 1000;
    result = data !== '' ? data.replace(/\s+/g, '').length : 0;
    return result ? maxCharacters - result : maxCharacters;
  }

  formSubmit() {
    this.createReviewSubject$.next(this.formGroup.getRawValue());
  }

  maxWordsValidation(): ValidatorFn {
    return ({ value }: FormControl): ValidationErrors | null => {
      const charactersCounterValue = value ? this.countCharacters(value) : 0;
      return charactersCounterValue < 0 ? { invalidLength: true } : null;
    };
  }

  openWritingTips() {
    const buttons = [
      {
        label: 'FORM.BUTTON.OK',
        type: ConfirmButtonType.Dismiss,
        className: 'btn btn-primary mr-2 min-w-100',
      },
    ];

    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.ACCOUNT.WRITE_A_REVIEW.MODALS.TIPS_FOR_WRITING.HEADER',
      body: this.writingTips,
      buttons,
      centered: true,
      size: 'lg',
    }).pipe(
      catchError(() => EMPTY)
    );
  }

  openSelectRepresentative() {
    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.ACCOUNT.WRITE_A_REVIEW.SELECT_REPRESENTATIVE',
      body: this.selectRepresentative,
      buttons: [ OkButtonLg ],
      centered: true,
      size: 'lg',
    }).pipe(
      catchError(() => EMPTY)
    );
  }

  setActiveReview(id) {
    this.reviewService.selectReview(id);
  }
}
