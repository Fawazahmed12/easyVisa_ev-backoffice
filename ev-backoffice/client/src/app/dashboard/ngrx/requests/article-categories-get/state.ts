import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { DashboardRequestService } from '../request.service';


export const articleCategoriesGetRequestHandler = createRequestHandler('GetArticleCategoriesRequest');

export function articleCategoriesGetRequestReducer(state, action) {
  return articleCategoriesGetRequestHandler.reducer(state, action);
}

@Injectable()
export class ArticleCategoriesGetRequestEffects {

  @Effect()
  articleCategoriesGet$: Observable<Action> = articleCategoriesGetRequestHandler.effect(
    this.actions$,
    this.dashboardRequestService.articleCategoriesGet.bind(this.dashboardRequestService)
  );

  constructor(
    private actions$: Actions,
    private dashboardRequestService: DashboardRequestService,
  ) {
  }
}
