import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';

import { switchMap } from 'rxjs/operators';

import { ModalService } from '../../../services/modal.service';

import { createRequestHandler, RequestFailAction } from '../../utils';

import { OrganizationsRequestService } from '../request.service';

export const affiliatedOrganizationsGetRequestHandler = createRequestHandler('Affiliated Organizations Get');

export function affiliatedOrganizationsRequestReducer(state, action) {
  return affiliatedOrganizationsGetRequestHandler.reducer(state, action);
}

@Injectable()
export class AffiliatedOrganizationsGetRequestEffects {

  @Effect()
  affiliatedOrganizationsGetSelfData$: Observable<Action> = affiliatedOrganizationsGetRequestHandler.effect(
    this.actions$,
    this.organizationsRequestService.affiliatedOrganizationsGetRequest.bind(this.organizationsRequestService)
  );

  @Effect({dispatch: false})
  showErrorModal$: Observable<Action> = this.actions$.pipe(
    ofType(affiliatedOrganizationsGetRequestHandler.ActionTypes.REQUEST_FAIL),
    switchMap(({payload}: RequestFailAction<any>) => this.modalService.showErrorModal(payload.error.errors || payload.error))
  );

  constructor(
    private actions$: Actions,
    private modalService: ModalService,
    private organizationsRequestService: OrganizationsRequestService
  ) {
  }
}
