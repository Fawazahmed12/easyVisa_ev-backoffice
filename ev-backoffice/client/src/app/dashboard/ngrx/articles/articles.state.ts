import { createEntityAdapter, EntityAdapter, EntityState } from '@ngrx/entity';
import { createFeatureSelector, createSelector } from '@ngrx/store';

import { Article } from '../../articles/models/article.model';
import { ArticleCategory } from '../../articles/models/article-category.model';

export const ARTICLES = 'Articles';

export interface ArticlesState extends EntityState<Article> {
  activeArticleId: number;
  articleCategories: ArticleCategory[];
  totalArticles: string;
}

export const  adapter: EntityAdapter<Article> = createEntityAdapter<Article>();

export const {selectAll, selectEntities} = adapter.getSelectors();

export const selectArticles = selectAll;

export const selectArticlesEntities = selectEntities;

export const selectArticlesState = createFeatureSelector<ArticlesState>(ARTICLES);

export const selectActiveArticleId = ({activeArticleId}: ArticlesState) => activeArticleId;
export const selectArticleCategories = ({articleCategories}: ArticlesState) => articleCategories;
export const selectTotalArticles = ({totalArticles}: ArticlesState) => totalArticles;

export const selectActiveArticle = createSelector(
  selectArticlesEntities,
  selectActiveArticleId,
  (articlesEntities, articleId) => articlesEntities[articleId]
);
