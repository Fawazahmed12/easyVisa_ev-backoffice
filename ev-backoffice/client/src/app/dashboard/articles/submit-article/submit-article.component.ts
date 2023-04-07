import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { QuillEditorComponent } from 'ngx-quill';
import { catchError, filter, map, startWith, switchMap, tap, withLatestFrom } from 'rxjs/operators';
import { EMPTY, Observable, of, Subject } from 'rxjs';

import { ModalService, OrganizationService, UserService } from '../../../core/services';
import { OkButtonLg } from '../../../core/modals/confirm-modal/confirm-modal.component';
import { RequestState } from '../../../core/ngrx/utils';

import { ArticlesService } from '../articles.service';
import { categoryList } from '../mock/article-categories.mock';
import { ArticleCategory, ArticleCategoryInitial } from '../models/article-category.model';

@Component({
  selector: 'app-submit-article',
  templateUrl: './submit-article.component.html',
  styleUrls: ['./submit-article.component.scss']
})

@DestroySubscribers()
export class SubmitArticleComponent implements OnInit, AddSubscribers {
  @ViewChild('editor', { static: true }) editor: QuillEditorComponent;
  @ViewChild('articleBonuses', { static: true }) articleBonuses;
  @ViewChild('articleWarning', { static: true }) articleWarning;

  private subscribers: any = {};

  newArticleForm: FormGroup;
  remainedCharacters$: Observable<number>;
  wordsCounter$: Observable<number>;
  postArticle$: Subject<any> = new Subject();
  currentRepresentativeId$: Observable<number>;
  articleCategories$: Observable<ArticleCategoryInitial[]>;
  categoryList = categoryList;
  parsedCategoryList$: Observable<ArticleCategory[]>;
  getArticleCategoriesGetRequest$: Observable<RequestState<ArticleCategoryInitial[]>>;
  isMe$: Observable<boolean>;

  formats = [
    'background',
    'bold',
    'color',
    'font',
    'code',
    'italic',
    'link',
    'size',
    'strike',
    'script',
    'underline',
    'blockquote',
    'header',
    'indent',
    'list',
    'align',
    'direction',
    'code-block',
    'formula'
  ];

  constructor(
    private router: Router,
    private articlesService: ArticlesService,
    private modalService: ModalService,
    private organizationService: OrganizationService,
    private userService: UserService,
  ) {
    this.currentRepresentativeId$ = this.organizationService.currentRepresentativeId$;
    this.articleCategories$ = this.articlesService.articleCategories$;
    this.createFormGroup();
    this.articlesService.createArticleFormGroupSnapShot(this.newArticleForm.value);
  }

  get titleControl() {
    return this.newArticleForm.get('title');
  }

  get contentControl() {
    return this.newArticleForm.get('content');
  }

  get locationIdControl() {
    return this.newArticleForm.get('locationId');
  }


  ngOnInit() {
    this.articlesService.getArticleCategories();
    this.getArticleCategoriesGetRequest$ = this.articlesService.getArticleCategoriesGetRequest$;
    this.isMe$ = this.userService.isCurrentRepresentativeMe$;
    this.remainedCharacters$ = this.titleControl.valueChanges.pipe(
      map((title: string) => title.length <= 100 ? title.length : 100)
    );

    this.wordsCounter$ = this.contentControl.valueChanges.pipe(
      startWith(''),
      map((text: string) => text ? this.wordsCounter(text) : 0)
    );

    this.parsedCategoryList$ = this.articleCategories$.pipe(
      filter(data => !!data && !!data.length),
      map(articleCategories => [...this.getParent(articleCategories)])
    );
  }

  addSubscribers() {
    this.subscribers.addNewArticleSubscription = this.postArticle$.pipe(
      // To open warning popup if invalid
      switchMap(() =>
        this.newArticleForm.invalid ?
          this.openArticleWarning().pipe(
            tap((res) => {
              this.articlesService.canOut = false;
            }),
            switchMap(() => EMPTY))
          : of(true)
      ),
      withLatestFrom(
        this.currentRepresentativeId$,
        this.organizationService.activeOrganizationId$
      ),
      switchMap(([, representativeId, organizationId]) => {
        const newArticle = {
          ...this.newArticleForm.value,
          representativeId,
          organizationId: +organizationId,
          locationName: this.getName(this.locationIdControl.value)
        };
        return this.articlesService.postArticle(newArticle).pipe(
          catchError((error: HttpErrorResponse) => EMPTY)
        );
      })
    ).subscribe(() => this.router.navigate(['/dashboard/articles']));

    this.subscribers.newArticleFormSubscription = this.newArticleForm.valueChanges
    .subscribe((res) => {
        const result = {
          ...res,
          title: !!res.title ? res.title : null,
          content: !!res.content ? res.content : null,
          location: !!res.location ? res.location : null,
        };
        this.articlesService.createArticleFormGroupSnapShot(result);
      }
    );
  }

  getParent(array: any[]) {
    return array.filter((i) => !i.parent_target_id).map((item) => {
        const nested = 0;
        const foundedChildren = this.getChild(item, array, nested);
        const isFoundedChild = !!foundedChildren.length;
        return isFoundedChild ? {...item, nested, children: this.getChild(item, array, nested)} : {...item, nested};
      }
    );
  }

  getChild(
    item: { tid: string; parent_target_id: string },
    array: { tid: string; parent_target_id: string }[],
    parentNesting: number
  ) {
    const childArray = array.filter((child) => item.tid === child.parent_target_id);
    return childArray.map((child) => {
        const nested = parentNesting + 1;
        const foundedChild = this.getChild(child, array, nested);
        const isFoundedChild = !!foundedChild.length;
        return isFoundedChild ? ({
          ...child,
          children: this.getChild(child, array, nested),
          nested,
        }) : {
          ...child,
          nested,
        };
      }
    );
  }

  getName(item: string) {
    const childItem = this.categoryList.find(i => i.tid === item);
    const www = this.getParentsNames(childItem);
    return www.join(' > ');
  }

  getParentsNames(
    item: { tid: string; parent_target_id: string; name: string },
  ) {
    const foundParent = this.categoryList.find(i => i.tid === item.parent_target_id);
    return !!foundParent ? [...this.getParentsNames(foundParent), item.name] : [item.name];
  }

  wordsCounter(text) {
    return text.replace(/<\/p>/g, '')
    .replace(/(^\s*)|(\s*$)/gi, '')
    .replace(/[ ]{2,}/gi, ' ')
    .replace(/\n /, '\n')
    .split(' ')
    .filter((str) => str !== '')
      .length;
  }

  formSubmit() {
    this.articlesService.canOut = true;
    this.postArticle$.next();
  }

  createFormGroup() {
    this.newArticleForm = new FormGroup({
      locationId: new FormControl(null, [Validators.required]),
      title: new FormControl(null,
        [
          Validators.required,
          Validators.maxLength(100)
        ]),
      content: new FormControl(null,
        [
          Validators.required,
          this.minWordsValidation()
        ]),
    });
  }

  minWordsValidation(): ValidatorFn {
    return ({value}: FormControl): ValidationErrors | null => {
      const wordsCounterValue = value ? value.trim().split(/\s+/).length : 0;
      return wordsCounterValue < 600 ? {invalidLength: true} : null;
    };
  }

  openArticleBonuses() {
    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.DASHBOARD.ARTICLES.ARTICLE_BONUSES_MODAL.HEADER',
      body: this.articleBonuses,
      buttons: [OkButtonLg],
      centered: true,
      size: 'lg',
    });
  }

  openArticleWarning() {
    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.DASHBOARD.ARTICLES.ARTICLE_BONUSES_MODAL.HEADER',
      body: this.articleWarning,
      buttons: [OkButtonLg],
      centered: true,
      size: 'lg',
    }).pipe(
      catchError(err => of(true))
    );
  }

  goBackToArticle(){
    this.articlesService.canOut = false;
    this.router.navigate(['/dashboard/articles']);
  }
}
