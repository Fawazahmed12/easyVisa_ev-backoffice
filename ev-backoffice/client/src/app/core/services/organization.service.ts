import { Injectable } from '@angular/core';
import { FormControl } from '@angular/forms';

import { select, Store } from '@ngrx/store';
import { Dictionary } from '@ngrx/entity/src/models';

import { delayWhen, filter, map, multicast, pluck, share, skip, switchMap, withLatestFrom } from 'rxjs/operators';
import { combineLatest, ConnectableObservable, Observable, of, ReplaySubject } from 'rxjs';

import { State } from '../ngrx/state';
import { throwIfRequestFailError } from '../ngrx/utils/rxjs-utils';
import { RequestState } from '../ngrx/utils';
import { CookieService } from 'ngx-cookie-service';

import { EmployeePosition } from '../../account/permissions/models/employee-position.enum';

import { Organization } from '../models/organization.model';
import { Attorney, AttorneyMenu } from '../models/attorney.model';
import {
  attorneysValidatePostRequestHandler,
  selectAttorneysPatchRequestState,
  selectAttorneysValidatePostRequestState,
  selectFeeScheduleGetRequestState,
  selectRepresentativesGetRequestState,
  selectRepresentativesMenuGetRequestState
} from '../ngrx/representatives-requests/state';
import {
  getActiveOrganization,
  getActiveOrganizationId,
  getAffiliatedOrganizations,
  getCurrentPosition,
  getIsAdmin,
  getOrganizations,
  getWithoutOrganizations
} from '../ngrx/organizations/organizations.state';
import {
  getCurrentRepresentative,
  getCurrentRepresentativeFeeSchedule,
  getCurrentRepresentativeId,
  getCurrentRepresentativeUserId,
  getFeeScheduleEntities,
  getRepresentativeEntities,
  getRepresentatives,
  getRepresentativesMenu,
} from '../ngrx/representatives/representatives.state';
import { GetFeeSchedule, UpdateCurrentRepresentativeId } from '../ngrx/representatives/representatives.actions';
import { attorneyPatchRequestHandler } from '../ngrx/representatives-requests/attorney-patch/state';

import { ChangeActiveOrganization, GetAffiliatedOrganizations, GetMenuOrganizations } from '../ngrx/organizations/organizations.actions';
import { selectAffiliatedOrganizationsGetRequestState, selectMenuOrganizationsGetRequestState } from '../ngrx/organizations-requests/state';
import { OrganizationType } from '../models/organization-type.enum';
import { FeeSchedule } from '../models/fee-schedule.model';

export const activeOrganizationId = 'ev-active-organization-id';
export const currentRepresentativeId = 'ev-current-representative-id';


@Injectable()
export class OrganizationService {

  representativesRequestState$: Observable<RequestState<Attorney[]>>;
  representativesMenuRequestState$: Observable<RequestState<AttorneyMenu[]>>;
  verifyAttorneyRequestState$: Observable<RequestState<{ representativeId: number; organizations: Organization[] }>>;
  representatives$: Observable<Attorney[]>;
  representativesMenu$: Observable<AttorneyMenu[]>;
  representativeEntities$: Observable<Dictionary<Attorney>>;
  currentRepresentativeId$: Observable<number>;
  currentRepresentative$: Observable<AttorneyMenu>;
  activeOrganizationId$: Observable<string>;
  activeOrganization$: Observable<Organization>;
  isAdmin$: Observable<boolean>;
  isLawFirm$: Observable<boolean>;
  updateAttorneyRequest$: Observable<RequestState<Attorney>>;
  representativeIdControl = new FormControl();
  organizationIdControl = new FormControl();
  organizations$: Observable<Organization[]>;
  affiliatedOrganizations$: Observable<Organization[]>;
  affiliatedOrganizationsGetRequest$: Observable<RequestState<Organization[]>>;
  organizationsGetRequest$: Observable<RequestState<Organization[]>>;
  currentRepresentativeFeeSchedule$: Observable<FeeSchedule[]>;
  currentRepresentativeFeeScheduleGetRequest$: Observable<RequestState<FeeSchedule[]>>;
  currentPosition$: Observable<EmployeePosition>;
  withoutOrganizations$: Observable<boolean>;
  currentRepresentativeUserId$: Observable<number>;
  currentRepIdOrgId$: Observable<number[]>;
  feeScheduleEntities$: Observable<Dictionary<FeeSchedule[]>>;

  constructor(
    private store: Store<State>,
    private cookieService: CookieService,
  ) {
    this.representativesRequestState$ = this.store.pipe(select(selectRepresentativesGetRequestState));
    this.representativesMenuRequestState$ = this.store.pipe(select(selectRepresentativesMenuGetRequestState));
    this.currentRepresentativeFeeSchedule$ = this.store.pipe(select(getCurrentRepresentativeFeeSchedule));
    this.currentRepresentativeFeeScheduleGetRequest$ = this.store.pipe(select(selectFeeScheduleGetRequestState));
    this.currentRepresentativeUserId$ = this.store.pipe(select(getCurrentRepresentativeUserId));
    this.verifyAttorneyRequestState$ = this.store.pipe(select(selectAttorneysValidatePostRequestState));
    this.representatives$ = this.store.pipe(select(getRepresentatives));
    this.representativesMenu$ = this.store.pipe(select(getRepresentativesMenu));
    this.representativeEntities$ = this.store.pipe(select(getRepresentativeEntities));
    this.currentRepresentativeId$ = this.store.pipe(select(getCurrentRepresentativeId));
    this.currentRepresentative$ = this.store.pipe(select(getCurrentRepresentative));
    this.activeOrganizationId$ = this.store.pipe(select(getActiveOrganizationId));
    this.activeOrganization$ = this.store.pipe(select(getActiveOrganization));
    this.updateAttorneyRequest$ = this.store.pipe(select(selectAttorneysPatchRequestState));
    this.isAdmin$ = this.store.pipe(select(getIsAdmin));
    this.organizations$ = this.store.pipe(select(getOrganizations));
    this.affiliatedOrganizations$ = this.store.pipe(select(getAffiliatedOrganizations));
    this.affiliatedOrganizationsGetRequest$ = this.store.pipe(select(selectAffiliatedOrganizationsGetRequestState));
    this.organizationsGetRequest$ = this.store.pipe(select(selectMenuOrganizationsGetRequestState));
    this.isLawFirm$ = this.activeOrganization$.pipe(
      filter((organization) => !!organization),
      map((organization) => organization.organizationType === OrganizationType.LAW_FIRM)
    );
    this.currentPosition$ = this.store.pipe(select(getCurrentPosition));
    this.withoutOrganizations$ = this.store.pipe(select(getWithoutOrganizations));
    this.feeScheduleEntities$ = this.store.pipe(select(getFeeScheduleEntities));


    combineLatest([
      this.activeOrganizationId$.pipe(filter(id => !!id)),
      this.organizations$.pipe(filter(organizations => !!organizations.length))
    ]).subscribe(([id, organizations]) => {
      this.organizationIdControl.patchValue(id, { emitEvent: false });
      if (organizations.length === 1) {
        this.organizationIdControl.disable({ emitEvent: false });
      } else {
        this.organizationIdControl.enable({ emitEvent: false });
      }
    });

    combineLatest([
      this.currentRepresentativeId$,
      this.representativesMenu$
    ]).pipe(
      filter(([id, rep]) => !!id && !!rep),
      withLatestFrom(this.activeOrganization$)
    ).subscribe(([[id, representatives], activeOrganization]) => {
      this.representativeIdControl.patchValue(id, { emitEvent: false });
      if (representatives.length === 1 && activeOrganization.organizationType !== OrganizationType.LAW_FIRM) {
        this.representativeIdControl.disable({ emitEvent: false });
      } else {
        this.representativeIdControl.enable({ emitEvent: false });
      }
    });

    const connectableCurrentOrgId$ = this.activeOrganizationId$.pipe(
      filter(id => !!id),
      map(id => parseInt(id, 10)),
      delayWhen(() =>
        this.representativesMenuRequestState$.pipe(
          skip(1),
          pluck('loading'),
          filter(loading => loading === false),
        )),
      switchMap(orgId => this.currentRepresentativeId$.pipe(
        filter(repId => typeof repId !== 'undefined'),
        map(repId => [repId, orgId]),
      )),
      multicast(new ReplaySubject(1)),
    ) as ConnectableObservable<number[]>;
    this.currentRepIdOrgId$ = connectableCurrentOrgId$;
    connectableCurrentOrgId$.connect();
  }

  updateAttorney(data) {
    this.store.dispatch(attorneyPatchRequestHandler.requestAction(data));
    return this.updateAttorneyRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  getAffiliatedOrganizations() {
    this.store.dispatch(new GetAffiliatedOrganizations());
    return this.affiliatedOrganizationsGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  UpdateCurrentRepresentative(id) {
    this.store.dispatch(new UpdateCurrentRepresentativeId(id));
  }

  verifyAttorney(data: { email: string; easyVisaId: string }): Observable<{ representativeId: number; organizations: Organization[] }> {
    this.store.dispatch(attorneysValidatePostRequestHandler.requestAction(data));
    return this.verifyAttorneyRequestState$.pipe(
      filter((state) => !state.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  hasAccessByPosition(positions: EmployeePosition[] = []) {
    return this.currentPosition$.pipe(
      withLatestFrom(this.isAdmin$),
      filter(([currentPosition]) => !!currentPosition),
      map(([currentPosition, isAdmin]) => !!positions.includes(currentPosition) || isAdmin),
    );
  }

  changeActiveOrganizationAction(id) {
    if (id) {
      this.store.dispatch(new ChangeActiveOrganization(id));
      this.store.dispatch(new UpdateCurrentRepresentativeId(undefined));
    }
  }

  getActiveOrganizationId() {
    return of(this.cookieService.get(activeOrganizationId));
  }

  setActiveOrganizationId(id) {
    this.cookieService.set(activeOrganizationId, id, 1, '/', null, null, 'Strict');
  }

  getCurrentRepresentativeId() {
    return of(this.cookieService.get(currentRepresentativeId));
  }

  setCurrentRepresentativeId(id) {
    this.cookieService.set(currentRepresentativeId, id, 1, '/', null, null, 'Strict');
  }

  removeCurrentRepresentativeId() {
    // TODO: used to remove the cookie. remove when update to new version of ngx-cookie-service
    this.cookieService.set(currentRepresentativeId, '', -1, '/', null, null, 'Strict');
  }

  getFeeSchedule(id) {
    this.store.dispatch(new GetFeeSchedule(id));
    return this.currentRepresentativeFeeScheduleGetRequest$.pipe(
      filter((state) => !state.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  getMenuOrganizations() {
    this.store.dispatch(new GetMenuOrganizations());
  }

  getCurrentFeeScheduleByRepId$(representativeId) {
    return this.feeScheduleEntities$.pipe(
      map(feeScheduleEntities => feeScheduleEntities[ representativeId ]),
    );
  }
}
