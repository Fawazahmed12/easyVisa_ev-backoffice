import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';
import { PackagesRequestService } from '../request.service';


export const packagesTransferByApplicantPostRequestHandler = createRequestHandler('PostPackagesTransferByApplicantRequest');

export function packagesTransferByApplicantPostRequestReducer(state, action) {
  return packagesTransferByApplicantPostRequestHandler.reducer(state, action);
}

@Injectable()
export class PackagesTransferByApplicantPostRequestEffects {

  @Effect()
  packagesTransfer$: Observable<Action> = packagesTransferByApplicantPostRequestHandler.effect(
    this.actions$,
    this.packagesRequestService.packagesTransferByApplicantPostRequest.bind(this.packagesRequestService)
  );

  constructor(
    private actions$: Actions,
    private packagesRequestService: PackagesRequestService,
  ) {
  }
}
