import { Injectable } from '@angular/core';

import { EMPTY, Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';

import { RequestFailAction, RequestSuccessAction } from '../../../core/ngrx/utils';

import {
  ArticlesActionTypes, GetArticleCategories, GetArticleCategoriesSuccess,
  GetArticles,
  GetArticlesSuccess,
  PostArticle,
  PostArticleSuccess
} from './articles.actions';
import { Article } from '../../articles/models/article.model';

import { articlesGetRequestHandler } from '../requests/articles-get/state';
import { articlePostRequestHandler } from '../requests/article-post/state';
import { articleCategoriesGetRequestHandler } from '../requests/article-categories-get/state';
import { ArticleCategory } from '../../articles/models/article-category.model';
import { catchError } from 'rxjs/operators';
import { ModalService } from '../../../core/services';

@Injectable()
export class ArticlesEffects {

  @Effect()
  getArticles$: Observable<Action> = this.actions$.pipe(
    ofType(ArticlesActionTypes.GetArticles),
    map(({payload}: GetArticles) => articlesGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getArticlesSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(articlesGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<Article[]>) => new GetArticlesSuccess(payload))
  );

  @Effect({dispatch: false})
  getArticlesFail$: Observable<Action> = this.actions$.pipe(
    ofType(articlesGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  getArticleCategories$: Observable<Action> = this.actions$.pipe(
    ofType(ArticlesActionTypes.GetArticleCategories),
    map(() => articleCategoriesGetRequestHandler.requestAction())
  );

  @Effect()
  getArticleCategoriesSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(articleCategoriesGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<ArticleCategory[]>) => new GetArticleCategoriesSuccess(payload))
  );

  @Effect({dispatch: false})
  getArticleCategoriesFail$: Observable<Action> = this.actions$.pipe(
    ofType(articleCategoriesGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  postArticle$: Observable<Action> = this.actions$.pipe(
    ofType(ArticlesActionTypes.PostArticle),
    map(({payload}: PostArticle) => articlePostRequestHandler.requestAction(payload))
  );

  @Effect()
  postArticlesSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(articlePostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<Article>) => new PostArticleSuccess(payload))
  );

  @Effect({dispatch: false})
  postArticlesFail$: Observable<Action> = this.actions$.pipe(
    ofType(articlePostRequestHandler.ActionTypes.REQUEST_FAIL),
    switchMap(({payload}: RequestFailAction<any>) =>
      this.modalService.showErrorModal(payload.error.errors).pipe(
        catchError((err) => EMPTY),
      )
    )
  );

  constructor(
    private actions$: Actions,
    private modalService: ModalService,
  ) {
  }

}
