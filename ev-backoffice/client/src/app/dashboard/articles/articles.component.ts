import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { DatePipe } from '@angular/common';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { combineLatest, EMPTY, Observable } from 'rxjs';
import { catchError, map, skip, switchMap, take, withLatestFrom } from 'rxjs/operators';

import { ModalService, OrganizationService, UserService } from '../../core/services';
import { TableDataFormat } from '../../task-queue/models/table-data-format.model';
import { Role } from '../../core/models/role.enum';
import { User } from '../../core/models/user.model';
import { EmployeePosition } from '../../account/permissions/models/employee-position.enum';
import { RequestState } from '../../core/ngrx/utils';

import { ArticlesService } from './articles.service';
import { Article } from './models/article.model';
import { PaginationService } from "../../core/services";

export interface ArticlesTableData {
  id: number;
  title: TableDataFormat;
  location: TableDataFormat;
  views: TableDataFormat;
  words: TableDataFormat;
  date: TableDataFormat;
  approved: TableDataFormat;
  active?: boolean;
}

@Component({
  selector: 'app-articles',
  templateUrl: './articles.component.html',
  styleUrls: ['./articles.component.scss']
})
@DestroySubscribers()
export class ArticlesComponent implements OnInit, OnDestroy, AddSubscribers {
  articles$: Observable<Article[]>;
  articlesTableData$: Observable<ArticlesTableData[]>;
  activeArticlesId$: Observable<number>;
  totalArticles$: Observable<string>;
  activeArticle$: Observable<Article>;
  activeOrganizationId$: Observable<number>;
  currentUser$: Observable<User>;
  isAttorney$: Observable<boolean>;
  getArticlesGetRequest$: Observable<RequestState<Article[]>>;
  isEvRole$: Observable<boolean>;
  submitBtnLabel$: Observable<string>;

  formGroup: FormGroup;

  private subscribers: any = {};

  EmployeePosition = EmployeePosition;

  headers = [
    {
      title: 'TEMPLATE.DASHBOARD.ARTICLES.TITLE',
      colName: 'title',
      sortBy: true,
      smallHeader: true,
      colClass: 'width-25 text-left'
    },
    {
      title: 'TEMPLATE.DASHBOARD.ARTICLES.LOCATION',
      colName: 'location',
      smallHeader: true,
      colClass: 'width-25 text-left'
    },
    {
      title: 'TEMPLATE.DASHBOARD.ARTICLES.VIEWS',
      colName: 'views',
      sortBy: true,
      smallHeader: true,
      colClass: 'text-center width-10'
    },
    {
      title: 'TEMPLATE.DASHBOARD.ARTICLES.WORDS',
      colName: 'words',
      sortBy: true,
      smallHeader: true,
      colClass: 'text-center width-10'
    },
    {
      title: 'TEMPLATE.DASHBOARD.ARTICLES.DATE_SUBMITTED',
      colName: 'date',
      sortBy: true,
      smallHeader: true,
      colClass: 'text-center width-15'
    },
    {
      title: 'TEMPLATE.DASHBOARD.ARTICLES.APPROVED',
      colName: 'approved',
      sortBy: true,
      smallHeader: true,
      colClass: 'text-center width-10'
    },
  ];

  get representativeIdFormControl() {
    return this.formGroup.get('representativeId');
  }

  get organizationIdFormControl() {
    return this.formGroup.get('organizationId');
  }

  get sortFormControl() {
    return this.formGroup.get('sort');
  }

  get orderFormControl() {
    return this.formGroup.get('order');
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

  constructor(
    private organizationService: OrganizationService,
    private articlesService: ArticlesService,
    private datePipe: DatePipe,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private userService: UserService,
    private modalService: ModalService,
    private paginationService: PaginationService
  ) {
    this.createFormGroup();
  }

  ngOnInit() {
    this.isEvRole$ = this.userService.isEvRole$;
    this.articles$ = this.articlesService.articles$;
    this.activeArticle$ = this.articlesService.activeArticle$;
    this.totalArticles$ = this.articlesService.totalArticles$;
    this.activeArticlesId$ = this.articlesService.activeArticlesId$;
    this.activeOrganizationId$ = this.articlesService.activeArticlesId$;
    this.isAttorney$ = this.userService.hasAccess([Role.ROLE_ATTORNEY]);
    this.currentUser$ = this.userService.currentUser$;
    this.getArticlesGetRequest$ = this.articlesService.getArticlesGetRequest$;

    this.articlesTableData$ = combineLatest([
      this.articles$,
      this.activeArticlesId$,
    ]).pipe(
      map(([articles, activeArticleId]) => articles.map((article) => ({
        id: article.id,
        approved: { data: '', class: this.setApprovedIcon(article.approved) },
        title: { data: article.title },
        location: { data: article.locationName },
        views: { data: !!article.views ? article.views.toString() : 0 },
        words: { data: article.words.toString() },
        date: { data: this.datePipe.transform(new Date(article.dateSubmitted), 'MM/dd/yyyy h:mm aaa') },
        active: activeArticleId && article.id === activeArticleId,
      })))
    );

    this.submitBtnLabel$ = this.isEvRole$.pipe(
      map(isEvRole => isEvRole ? 'TEMPLATE.DASHBOARD.ARTICLES.DISPOSITION' : 'TEMPLATE.DASHBOARD.ARTICLES.SUBMIT_NEW_ARTICLE')
    );
  }

  addSubscribers() {
    this.subscribers.historyNavigationSubscription$ = this.paginationService.getHistoryNavigationSubscription(this.offsetFormControl)

    this.subscribers.queryParamsMarketingSubscription = this.activatedRoute.queryParams.pipe(
      withLatestFrom(this.organizationService.currentRepIdOrgId$),
      take(1),
    ).subscribe(([params, [repId, orgId]]) => this.formGroup.patchValue({
      organizationId: parseInt(params?.organizationId, 10) || orgId,
      representativeId: parseInt(params?.representativeId, 10) || repId,
    }, { emitEvent: false }));

    this.subscribers.currentRepIdMarketingSubscription = this.organizationService.currentRepIdOrgId$.pipe(
      skip(1),
    ).subscribe(([representativeId, organizationId]) => this.formGroup.patchValue({ representativeId, organizationId }));

    this.subscribers.formGroupSubscription = this.formGroup.valueChanges.pipe(
      switchMap(() => this.articlesService.getArticles(this.formGroup.getRawValue()
        ).pipe(
          catchError(() => {
            this.router.navigate([]);
            return EMPTY;
          })
        )
      )
    ).subscribe();

    this.subscribers.articlesFormGroupSubscription = this.formGroup.valueChanges
      .subscribe(() => this.addQueryParamsToUrl(this.formGroup.getRawValue()));
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  createFormGroup() {
    this.formGroup = new FormGroup({
      sort: new FormControl('title'),
      order: new FormControl('asc'),
      representativeId: new FormControl(),
      organizationId: new FormControl(),
      offset: new FormControl(0),
      max: new FormControl(25),
    });
  }

  pageChange(page) {
    const offset = (page - 1) * this.maxFormControl.value;
    if (offset !== (this.offsetFormControl.value)) {
      this.offsetFormControl.patchValue(offset);
    }
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

  setApprovedIcon(approved) {
    switch (approved) {
      case true: {
        return 'fa fa-check text-primary';
      }
      case false: {
        return 'fa fa-close text-danger';
      }
      case null: {
        return 'fa fa-minus text-gray';
      }
      default: {
        return '';
      }
    }
  }

  setActiveArticle(article) {
    this.articlesService.setActiveArticleId(article.id);
  }

  canUserSubmit(roles): boolean {
    let allowedRoles = [Role.ROLE_ATTORNEY, Role.ROLE_OWNER];
    return allowedRoles.some((allowedRole) => roles.includes(allowedRole));
  }

  isSubmitDisabled(user): boolean {
    return !this.canUserSubmit(user.roles);
  }

  onSubmit() {
    this.currentUser$.pipe(take(1)).subscribe((currentUser) => {
      if (this.canUserSubmit(currentUser.roles)) {
        this.router.navigate(['/dashboard/articles/submit-article']);
      } else {
        this.modalService.showErrorModal(
          'FORM.ERROR.NOT_ALLOWED_TO_SUBMIT_ARTICLE'
        );
      }
    });
  }
}
