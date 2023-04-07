import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';

import { PackagesRequestService } from '../request.service';

export const packagesGetRequestHandler = createRequestHandler('GetPackagesRequest');

export function packagesGetRequestReducer(state, action) {
  return packagesGetRequestHandler.reducer(state, action);
}

@Injectable()
export class PackagesGetRequestEffects {

  @Effect()
  packagesData$: Observable<Action> = packagesGetRequestHandler.effect(
    this.actions$,
    this.packagesRequestService.packagesRequest.bind(this.packagesRequestService)
  );

  constructor(
    private actions$: Actions,
    private packagesRequestService: PackagesRequestService,
  ) {
  }
}
