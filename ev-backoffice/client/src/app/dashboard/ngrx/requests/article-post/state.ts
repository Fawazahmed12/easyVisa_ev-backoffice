import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { DashboardRequestService } from '../request.service';


export const articlePostRequestHandler = createRequestHandler('PostArticleRequest');

export function articlePostRequestReducer(state, action) {
  return articlePostRequestHandler.reducer(state, action);
}

@Injectable()
export class ArticlePostRequestEffects {

  @Effect()
  articlePost$: Observable<Action> = articlePostRequestHandler.effect(
    this.actions$,
    this.dashboardRequestService.articlePost.bind(this.dashboardRequestService)
  );

  constructor(
    private actions$: Actions,
    private dashboardRequestService: DashboardRequestService,
  ) {
  }
}
