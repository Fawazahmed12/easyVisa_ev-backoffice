import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';
import { PackagesRequestService } from '../request.service';


export const packagesTransferPostRequestHandler = createRequestHandler('PostPackagesTransferRequest');

export function packagesTransferPostRequestReducer(state, action) {
  return packagesTransferPostRequestHandler.reducer(state, action);
}

@Injectable()
export class PackagesTransferPostRequestEffects {

  @Effect()
  packagesTransfer$: Observable<Action> = packagesTransferPostRequestHandler.effect(
    this.actions$,
    this.packagesRequestService.packagesTransferPostRequest.bind(this.packagesRequestService)
  );

  constructor(
    private actions$: Actions,
    private packagesRequestService: PackagesRequestService,
  ) {
  }
}
