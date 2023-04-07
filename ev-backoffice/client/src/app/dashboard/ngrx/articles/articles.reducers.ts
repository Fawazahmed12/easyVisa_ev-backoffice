import {
  ArticlesActionsUnion,
  ArticlesActionTypes, GetArticleCategoriesSuccess,
  GetArticlesSuccess,
  PostArticleSuccess,
  SetActiveArticle
} from './articles.actions';
import { adapter, ArticlesState } from './articles.state';

export const initialState: ArticlesState = adapter.getInitialState({
  activeArticleId: null,
  articleCategories: null,
  totalArticles: null,
});

export function reducer(state = initialState, action: ArticlesActionsUnion) {
  switch (action.type) {

    case ArticlesActionTypes.GetArticlesSuccess: {
      const totalArticles = (action as GetArticlesSuccess).payload.headers.get('X-total-count');
      return {
        ...adapter.setAll((action as GetArticlesSuccess).payload.body, state),
        totalArticles
      };
    }

    case ArticlesActionTypes.SetActiveArticle: {
      return {
        ...state,
        activeArticleId: (action as SetActiveArticle).payload,
      };
    }

    case ArticlesActionTypes.PostArticleSuccess: {
      return {
        ...adapter.addOne((action as PostArticleSuccess).payload, state),
      };
    }

    case ArticlesActionTypes.GetArticleCategoriesSuccess: {
      return {
        ...state,
        articleCategories: (action as GetArticleCategoriesSuccess).payload,
      };
    }

    default: {
      return state;
    }
  }
}
