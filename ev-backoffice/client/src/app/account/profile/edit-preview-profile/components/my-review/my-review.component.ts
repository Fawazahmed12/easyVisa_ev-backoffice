import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { DatePipe } from '@angular/common';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { EMPTY, Observable, Subject } from 'rxjs';
import { catchError, filter, map, switchMap, take, withLatestFrom } from 'rxjs/operators';

import { TableDataFormat } from '../../../../../task-queue/models/table-data-format.model';
import { TableHeader } from '../../../../../components/table/models/table-header.model';
import { ModalService, OrganizationService } from '../../../../../core/services';

import { ReviewService } from '../../../../services/review.service';
import { Review } from '../../../../models/review.model';
import { PaginationService } from "../../../../../core/services";


export interface MyReviewTableData {
  id: number;
  rating: number;
  title: TableDataFormat;
  petitioner: TableDataFormat;
  date: TableDataFormat;
  replied: TableDataFormat;
}

@Component({
  selector: 'app-my-review',
  templateUrl: './my-review.component.html',
})
@DestroySubscribers()
export class MyReviewComponent implements OnInit, AddSubscribers {
  @ViewChild('writeReply', { static: true }) writeReply;

  reviews$: Observable<Review[]>;
  reviewsTableData$: Observable<MyReviewTableData[]>;
  activeReview$: Observable<Review>;
  headers: TableHeader[];
  totalReviews$: Observable<number>;
  totalFilteredReviews$: Observable<number>;
  countRating$: Observable<any>;
  reviewsRating$: Observable<any>;
  private openWriteReplyModalSubject$: Subject<number> = new Subject<number>();


  formGroup: FormGroup;

  private subscribers: any = {};

  get sortFormControl() {
    return this.formGroup.get('sort');
  }

  get orderFormControl() {
    return this.formGroup.get('order');
  }

  get ratingFormControl() {
    return this.formGroup.get('rating');
  }

  get maxFormControl() {
    return this.formGroup.get('max');
  }

  get offsetFormControl() {
    return this.formGroup.get('offset');
  }

  get page() {
    return (this.offsetFormControl.value / this.maxFormControl.value) + 1;
  }

  get disableSortingStarColumn() {
    return this.ratingFormControl.value === 'all' ? null : this.ratingFormControl.value;
  }

  constructor(
    private reviewService: ReviewService,
    private router: Router,
    private organizationService: OrganizationService,
    private activatedRoute: ActivatedRoute,
    private datePipe: DatePipe,
    private modalService: ModalService,
    private paginationService: PaginationService
  ) {
    this.createFormGroup();
  }

  ngOnInit() {
    this.headers = [
      {
        title: 'TEMPLATE.ACCOUNT.PROFILE.MY_REVIEW.RATING',
        colName: 'rating',
        sortBy: true,
        action: true,
        colClass: 'text-center width-15 p-0 border-0',
      },
      {
        title: 'TEMPLATE.ACCOUNT.PROFILE.MY_REVIEW.REVIEW_TITLE',
        colName: 'title',
        colClass: 'width-45 p-0 border-0',
      },
      {
        title: 'TEMPLATE.ACCOUNT.PROFILE.MY_REVIEW.PETITIONER',
        colName: 'petitioner',
        colClass: 'width-15 p-0 border-0',
      },
      {
        title: 'TEMPLATE.ACCOUNT.PROFILE.MY_REVIEW.DATE',
        colName: 'date',
        sortBy: true,
        colClass: 'text-right width-15 p-0 border-0',
      },
      {
        title: 'TEMPLATE.ACCOUNT.PROFILE.MY_REVIEW.REPLIED',
        colName: 'replied',
        sortBy: true,
        colClass: 'text-center width-10 p-0 border-0',
      },
    ];
    this.totalReviews$ = this.reviewService.totalReviews$;
    this.totalFilteredReviews$ = this.reviewService.totalFilteredReviews$;
    this.activeReview$ = this.reviewService.activeReview$;
    this.reviewsRating$ = this.reviewService.reviewsRating$;
    this.reviews$ = this.reviewService.reviews$;

    this.reviewsTableData$ = this.reviews$.pipe(
      map((reviews) => reviews.map((review) => ({
        id: review.id,
        rating: review.rating,
        title: { data: review.title, class: this.setBoldClass(review.read) },
        petitioner: { data: review.reviewer, class: this.setBoldClass(review.read) },
        date: {
          data: this.datePipe.transform(new Date(review.dateCreated), 'MM/dd/yyyy hh:mm aaa'),
          class: this.setBoldClass(review.read)
        },
        replied: { data: '', class: review.reply ? 'fa fa-check' : '' },
      })))
    );

    this.countRating$ = this.reviewsRating$.pipe(
      filter((reviewsRating) => !!reviewsRating),
      withLatestFrom(this.totalReviews$),
      map(([ratings, total]) => {
        let res = 0;
        const ratePercentage = ratings.map((rating) => {
          res += (rating.value * rating.count) / total;
          return {
            value: rating.value,
            count: ((rating.count / total) * 100),
          };
        });
        return {
          averageRate: res,
          ratePercentage
        };
      })
    );
  }

  addSubscribers() {
    this.subscribers.historyNavigationSubscription$ = this.paginationService.getHistoryNavigationSubscription(this.offsetFormControl)

    this.subscribers.queryParamsSubscription = this.activatedRoute.queryParams.pipe(
      filter((params) => !!params),
      map((params) => {
        if (params.rating) {
          return {
            ...params,
            rating: params.rating === 'all' ? null : parseInt(params.rating, 10)
          };
        }
        return params;
      }),
      take(1),
    ).subscribe((res) => this.formGroup.patchValue(res, { emitEvent: false }));

    this.subscribers.getReviewsFormGroupSubscription = this.formGroup.valueChanges.pipe(
      switchMap(() => this.reviewService.getReviews(
          this.formGroup.getRawValue().rating === 'all' ?
            {
              ...this.formGroup.getRawValue(),
              rating: null
            }
            :
            this.formGroup.getRawValue()
        ).pipe(
          catchError(() => EMPTY),
          take(1),
        )
      ),
    ).subscribe(() => this.addQueryParamsToUrl(this.formGroup.getRawValue()));

    this.subscribers.openWriteReplyModalSubjectSubscription = this.openWriteReplyModalSubject$
      .subscribe(() => this.openWriteReplyModal());
  }

  addQueryParamsToUrl(params?) {
    this.router.navigate(['./'], { relativeTo: this.activatedRoute, queryParams: { ...params } });
  }

  sortBy(colName) {
    if (colName !== this.sortFormControl.value) {
      this.formGroup.patchValue({ sort: colName });
    } else {
      this.formGroup.patchValue({ order: this.orderFormControl.value === 'asc' ? 'desc' : 'asc' });
    }
  }

  pageChange(page) {
    const offset = (page - 1) * this.maxFormControl.value;
    if (offset !== (this.offsetFormControl.value)) {
      this.offsetFormControl.patchValue(offset);
    }
  }

  setBoldClass(read) {
    return read ? '' : 'font-weight-bolder';
  }

  changeFilterByRating(item?) {
    this.ratingFormControl.patchValue(item ? item.rating || item : 'all');
  }

  openWriteReply(item?) {
    this.reviewService.selectReview(item ? item.id : null);
    this.openWriteReplyModalSubject$.next();
  }

  createFormGroup(data?) {
    this.formGroup = new FormGroup({
      sort: new FormControl('date'),
      order: new FormControl('desc'),
      offset: new FormControl(data && data.offset ? data.offset : 0),
      max: new FormControl(data && data.max ? data.max : 25),
      rating: new FormControl(data?.rating),
    });
  }

  openWriteReplyModal() {
    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.ACCOUNT.PROFILE.MY_REVIEW.WRITE_REPLY',
      body: this.writeReply,
      buttons: [],
      centered: true,
      size: 'lg',
    }).pipe(
      catchError(() => EMPTY),
    );
  }
}
