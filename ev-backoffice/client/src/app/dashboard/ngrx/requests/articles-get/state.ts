import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { DashboardRequestService } from '../request.service';


export const articlesGetRequestHandler = createRequestHandler('GetArticlesRequest');

export function articlesGetRequestReducer(state, action) {
  return articlesGetRequestHandler.reducer(state, action);
}

@Injectable()
export class ArticlesGetRequestEffects {

  @Effect()
  articlesGet$: Observable<Action> = articlesGetRequestHandler.effect(
    this.actions$,
    this.dashboardRequestService.articlesGet.bind(this.dashboardRequestService)
  );

  constructor(
    private actions$: Actions,
    private dashboardRequestService: DashboardRequestService,
  ) {
  }
}
