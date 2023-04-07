import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';
import { filter, map, mergeMap, pluck, switchMap, withLatestFrom } from 'rxjs/operators';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';

import { RequestFailAction, RequestSuccessAction } from '../../../../core/ngrx/utils';
import {
  GetDocumentProgress,
  GetDocumentProgressFailure,
  GetDocumentProgressSuccess,
  GetQuestionnaireProgress, GetQuestionnaireProgressFailure,
  GetQuestionnaireProgressSuccess,
  ProgressStatusesActionTypes
} from './progress-statuses.actions';
import { UserService } from '../../../../core/services';
import { GetActivePackageSuccess, PackagesActionTypes } from '../../../../core/ngrx/packages/packages.actions';
import { Role } from '../../../../core/models/role.enum';

import { ProgressStatus } from '../../models/progress-status.model';

import { questionnaireProgressGetRequestHandler } from '../requests/questionnaire-progress-get/state';
import { documentProgressGetRequestHandler } from '../requests/state';


@Injectable()
export class ProgressStatusesEffects {

  @Effect()
  getQuestionnaireProgressAfterSetActivePackage$: Observable<Action> = this.actions$.pipe(
    ofType(PackagesActionTypes.GetActivePackageSuccess),
    map(({payload}: GetActivePackageSuccess) => payload.id),
    withLatestFrom(this.userService.currentUser$.pipe(
      filter((user) => !!user),
      pluck('roles')
    )),
    filter(([, roles]: [number, Role[]]) => !!roles.some((userRole: Role) => userRole === Role.ROLE_USER)),
    mergeMap(([id, ]: [number, Role[]]) => [new GetQuestionnaireProgress(id), new GetDocumentProgress(id)])
  );

  @Effect()
  getQuestionnaireProgress$: Observable<Action> = this.actions$.pipe(
    ofType(ProgressStatusesActionTypes.GetQuestionnaireProgress),
    map(({payload}: GetQuestionnaireProgress) => questionnaireProgressGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getQuestionnaireProgressSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(questionnaireProgressGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<ProgressStatus[]>) => new GetQuestionnaireProgressSuccess(payload))
  );

  @Effect()
  getQuestionnaireProgressFail$: Observable<Action> = this.actions$.pipe(
    ofType(questionnaireProgressGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new GetQuestionnaireProgressFailure(payload))
  );

  @Effect()
  getDocumentProgress$: Observable<Action> = this.actions$.pipe(
    ofType(ProgressStatusesActionTypes.GetDocumentProgress),
    map(({payload}: GetQuestionnaireProgress) => documentProgressGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getDocumentProgressSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(documentProgressGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<ProgressStatus[]>) => new GetDocumentProgressSuccess(payload))
  );

  @Effect()
  getDocumentProgressFail$: Observable<Action> = this.actions$.pipe(
    ofType(documentProgressGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new GetDocumentProgressFailure(payload))
  );

  constructor(
    private actions$: Actions,
    private userService: UserService,
  ) {
  }

}
