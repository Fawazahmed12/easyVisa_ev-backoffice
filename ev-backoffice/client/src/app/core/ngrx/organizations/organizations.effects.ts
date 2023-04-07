import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';
import { filter, map, pluck, switchMap, tap } from 'rxjs/operators';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';

import { find as _find, head } from 'lodash-es';

import { Organization } from '../../models/organization.model';
import { OrganizationType } from '../../models/organization-type.enum';
import { OrganizationService } from '../../services';

import { GetRepresentativesMenu } from '../representatives/representatives.actions';
import { RequestFailAction, RequestSuccessAction } from '../utils';
import { menuOrganizationsGetRequestHandler } from '../organizations-requests/menu-organizations-get/state';

import {
  GetAffiliatedOrganizationsSuccess,
  GetMenuOrganizationsSuccess,
  OrganizationsActionTypes, SetActiveOrganization,
} from './organizations.actions';
import { affiliatedOrganizationsGetRequestHandler } from '../organizations-requests/affiliated-organizations-get/state';
import { GetOrganization } from '../../../account/ngrx/organization/organization.actions';
import { GetBenefitCategories } from '../config-data/config-data.actions';

@Injectable()
export class OrganizationsEffects {

  @Effect()
  getRepresentatives$: Observable<Action> = this.actions$.pipe(
    ofType(
      OrganizationsActionTypes.SetActiveOrganization,
      OrganizationsActionTypes.ChangeActiveOrganization,
    ),
    filter(({payload}: SetActiveOrganization) => !!payload),
    map(({payload}: SetActiveOrganization) => new GetRepresentativesMenu(payload)),
  );

  @Effect()
  getMenuOrganizations$: Observable<Action> = this.actions$.pipe(
    ofType(OrganizationsActionTypes.GetMenuOrganizations),
    map(() => menuOrganizationsGetRequestHandler.requestAction())
  );

  @Effect()
  getMenuOrganizationsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(menuOrganizationsGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<number>) => new GetMenuOrganizationsSuccess(payload))
  );

  @Effect({dispatch: false})
  getMenuOrganizationsFail$: Observable<Action> = this.actions$.pipe(
    ofType(menuOrganizationsGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  getAffiliatedOrganizations$: Observable<Action> = this.actions$.pipe(
    ofType(OrganizationsActionTypes.GetAffiliatedOrganizations),
    map(() => affiliatedOrganizationsGetRequestHandler.requestAction())
  );

  @Effect()
  getAffiliatedOrganizationsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(affiliatedOrganizationsGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<number>) => new GetAffiliatedOrganizationsSuccess(payload))
  );

  @Effect({dispatch: false})
  getAffiliatedOrganizationsFail$: Observable<Action> = this.actions$.pipe(
    ofType(affiliatedOrganizationsGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect({dispatch: false})
  setActiveOrganizationIdToLocal$: Observable<Action> = this.actions$.pipe(
    ofType(
      OrganizationsActionTypes.SetActiveOrganization,
      OrganizationsActionTypes.ChangeActiveOrganization,
    ),
    tap(({payload}: SetActiveOrganization) => {
      this.organizationService.setActiveOrganizationId(payload);
    })
  );

  @Effect()
  getOrganization$: Observable<Action> = this.actions$.pipe(
    ofType(
      OrganizationsActionTypes.SetActiveOrganization,
      OrganizationsActionTypes.ChangeActiveOrganization
    ),
    map(({payload}: SetActiveOrganization) => parseInt(payload, 10)),
    filter(id => !!id),
    map((id) => new GetOrganization(id))
  );

  @Effect()
  setActiveOrganization$: Observable<Action> = this.actions$.pipe(
    ofType(OrganizationsActionTypes.GetMenuOrganizationsSuccess),
    map(({payload}: GetMenuOrganizationsSuccess) => payload),
    filter(organizations => !!organizations.length),
    switchMap((organizations) => this.organizationService.getActiveOrganizationId().pipe(
      map( localActiveOrganizationId => [organizations, localActiveOrganizationId])
    )),
    map(([organizations, localActiveOrganizationId]: [Organization[], string]) => {
      if (localActiveOrganizationId) {
        const validOrganizationFromLs = _find(organizations, ['id', parseInt(localActiveOrganizationId, 10)]);
        return new SetActiveOrganization(validOrganizationFromLs?.id || head(organizations)?.id);
      } else {
        const findActiveOrganization = _find(organizations, ['organizationType', OrganizationType.LAW_FIRM]);
        return new SetActiveOrganization(findActiveOrganization?.id || head(organizations)?.id);
      }
    }),
  );

  @Effect()
  getBenefits$: Observable<Action> = this.actions$.pipe(
    ofType(OrganizationsActionTypes.GetMenuOrganizationsSuccess),
    map(({payload}: GetMenuOrganizationsSuccess) => payload),
    filter(organizations => !!organizations.length),
    map(() => new GetBenefitCategories())
  );

  constructor(
    private actions$: Actions,
    private organizationService: OrganizationService,
  ) {
  }
}
