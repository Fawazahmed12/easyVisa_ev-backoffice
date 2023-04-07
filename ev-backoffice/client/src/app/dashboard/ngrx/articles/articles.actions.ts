import { HttpResponse } from '@angular/common/http';
import { Action } from '@ngrx/store';

import { ARTICLES } from './articles.state';
import { Article } from '../../articles/models/article.model';
import { ArticleCategory } from '../../articles/models/article-category.model';

export const ArticlesActionTypes = {
  GetArticles: `[${ARTICLES}] Get Articles`,
  GetArticlesSuccess: `[${ARTICLES}] Get Articles Success`,
  GetArticlesFailure: `[${ARTICLES}] Get Articles Failure`,
  GetArticleCategories: `[${ARTICLES}] Get Categories`,
  GetArticleCategoriesSuccess: `[${ARTICLES}] Get Categories Success`,
  GetArticleCategoriesFailure: `[${ARTICLES}] Get Categories Failure`,
  PostArticle: `[${ARTICLES}] Post Article`,
  PostArticleSuccess: `[${ARTICLES}] Post Article Success`,
  PostArticleFailure: `[${ARTICLES}] Post Article Failure`,
  SetActiveArticle: `[${ARTICLES}] Set Active Article`,
};

export class GetArticles implements Action {
  readonly type = ArticlesActionTypes.GetArticles;

  constructor(public payload?: any) {
  }
}

export class GetArticlesSuccess implements Action {
  readonly type = ArticlesActionTypes.GetArticlesSuccess;

  constructor(public payload: HttpResponse<Article[]>) {
  }
}

export class GetArticlesFailure implements Action {
  readonly type = ArticlesActionTypes.GetArticlesFailure;

  constructor(public payload?: any) {
  }
}

export class GetArticleCategories implements Action {
  readonly type = ArticlesActionTypes.GetArticleCategories;
}

export class GetArticleCategoriesSuccess implements Action {
  readonly type = ArticlesActionTypes.GetArticleCategoriesSuccess;

  constructor(public payload: ArticleCategory[]) {
  }
}

export class GetArticleCategoriesFailure implements Action {
  readonly type = ArticlesActionTypes.GetArticleCategoriesFailure;

  constructor(public payload?: any) {
  }
}

export class PostArticle implements Action {
  readonly type = ArticlesActionTypes.PostArticle;

  constructor(public payload: any) {
  }
}

export class PostArticleSuccess implements Action {
  readonly type = ArticlesActionTypes.PostArticleSuccess;

  constructor(public payload: any) {
  }
}

export class PostArticleFailure implements Action {
  readonly type = ArticlesActionTypes.PostArticleFailure;

  constructor(public payload: any) {
  }
}

export class SetActiveArticle implements Action {
  readonly type = ArticlesActionTypes.SetActiveArticle;

  constructor(public payload: number) {
  }
}

export type ArticlesActionsUnion =
  | GetArticles
  | GetArticlesSuccess
  | GetArticlesFailure
  | GetArticleCategories
  | GetArticleCategoriesSuccess
  | GetArticleCategoriesFailure
  | SetActiveArticle
  | PostArticle
  | PostArticleSuccess
  | PostArticleFailure;
