import {Injectable} from '@angular/core';

import {select, Store} from '@ngrx/store';

import {Observable, of} from 'rxjs';
import {catchError, filter, share, take, tap} from 'rxjs/operators';
import {isEqual} from 'lodash-es';

import {throwIfRequestFailError} from '../../core/ngrx/utils/rxjs-utils';
import {State} from '../../core/ngrx/state';
import {RequestState} from '../../core/ngrx/utils';
import {ConfirmButtonType} from '../../core/modals/confirm-modal/confirm-modal.component';
import {ModalService} from '../../core/services';

import {
  getActiveArticles,
  getActiveArticlesId,
  getArticleCategories,
  getArticleCategoriesGetRequestState,
  getArticles,
  getArticlesGetRequestState,
  getTotalArticles,
  postArticlePostRequestState
} from '../ngrx/state';
import {GetArticleCategories, GetArticles, PostArticle, SetActiveArticle} from '../ngrx/articles/articles.actions';

import {Article} from './models/article.model';
import {ArticleCategoryInitial} from './models/article-category.model';


@Injectable()
export class ArticlesService {
  articles$: Observable<Article[]>;
  activeArticle$: Observable<Article>;
  activeArticlesId$: Observable<number>;
  totalArticles$: Observable<string>;
  articleCategories$: Observable<ArticleCategoryInitial[]>;
  getArticleCategoriesGetRequest$: Observable<RequestState<ArticleCategoryInitial[]>>;
  getArticlesGetRequest$: Observable<RequestState<Article[]>>;
  postArticlePostRequest$: Observable<RequestState<Article>>;
  canOut = false;
  articleSnapshot: {
    location: string;
    title: string;
    content: string;
  };

  defaultArticleSnapshot = {
    location: null,
    title: null,
    content: null
  };

  constructor(
    private store: Store<State>,
    private modalService: ModalService,
  ) {
    this.articles$ = this.store.pipe(select(getArticles));
    this.activeArticle$ = this.store.pipe(select(getActiveArticles));
    this.activeArticlesId$ = this.store.pipe(select(getActiveArticlesId));
    this.articleCategories$ = this.store.pipe(select(getArticleCategories));
    this.totalArticles$ = this.store.pipe(select(getTotalArticles));
    this.getArticleCategoriesGetRequest$ = this.store.pipe(select(getArticleCategoriesGetRequestState));
    this.getArticlesGetRequest$ = this.store.pipe(select(getArticlesGetRequestState));
    this.postArticlePostRequest$ = this.store.pipe(select(postArticlePostRequestState));
  }

  isArticleChanges() {
    if (this.canOut) {
      this.canOut = false;
      return true;
    } else if (this.checkFormGroupChanges()) {
      return this.openNewArticleWarningModal()
        .pipe(
          take(1),
          tap((res) => {
            if (res) {
              this.canOut = !this.canOut;
            }
          }),
          catchError((err) => of(true))
        );
    } else {
      return true;
    }
  }

  getArticles(data) {
    this.store.dispatch(new GetArticles(data));
    return this.getArticlesGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  postArticle(data) {
    this.store.dispatch(new PostArticle(data));
    return this.postArticlePostRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  setActiveArticleId(id) {
    this.store.dispatch(new SetActiveArticle(id));
  }

  createArticleFormGroupSnapShot(formGroupValue) {
    this.articleSnapshot = formGroupValue;
  }

  checkFormGroupChanges(): boolean {
    return !isEqual(this.articleSnapshot, this.defaultArticleSnapshot);
  }

  private openNewArticleWarningModal() {
    const buttons = [
      {
        label: 'FORM.BUTTON.CANCEL',
        type: ConfirmButtonType.Close,
        className: 'btn btn-primary mr-2 min-w-100',
        value: false
      },
      {
        label: 'FORM.BUTTON.OK',
        type: ConfirmButtonType.Close,
        className: 'btn btn-primary mr-2 min-w-100',
        value: true
      },
    ];

    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.DASHBOARD.ARTICLES.ARTICLE_CHANGES_DETECTED.HEADER',
      body: 'TEMPLATE.DASHBOARD.ARTICLES.ARTICLE_CHANGES_DETECTED.WARNING',
      buttons,
      centered: true,
      showCloseIcon: false,
      backdrop: 'static'
    });
  }

  getArticleCategories() {
    this.store.dispatch(new GetArticleCategories());
  }
}
