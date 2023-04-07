import { Action } from '@ngrx/store';

import { Organization } from '../../models/organization.model';

import { ORGANIZATIONS } from './organizations.state';


export const OrganizationsActionTypes = {
  SetActiveOrganization: `[${ORGANIZATIONS}] Set Active Organization`,
  ChangeActiveOrganization: `[${ORGANIZATIONS}] Change Active Organization`,
  UpdateOrganization: `[${ORGANIZATIONS}] Update Organization`,
  UpdateActiveOrganization: `[${ORGANIZATIONS}] Update Active Organization`,
  GetMenuOrganizations: `[${ORGANIZATIONS}] Get Menu Organizations`,
  GetMenuOrganizationsSuccess: `[${ORGANIZATIONS}] Get Menu Organizations Success`,
  GetMenuOrganizationsFailure: `[${ORGANIZATIONS}] Get Menu Organizations Failure`,
  GetAffiliatedOrganizations: `[${ORGANIZATIONS}] Get Affiliated Organizations`,
  GetAffiliatedOrganizationsSuccess: `[${ORGANIZATIONS}] Get Affiliated Organizations Success`,
  GetAffiliatedOrganizationsFailure: `[${ORGANIZATIONS}] Get Affiliated Organizations Failure`,
};

export class GetMenuOrganizations implements Action {
  readonly type = OrganizationsActionTypes.GetMenuOrganizations;
}

export class GetMenuOrganizationsSuccess implements Action {
  readonly type = OrganizationsActionTypes.GetMenuOrganizationsSuccess;

  constructor(public payload: Organization[]) {
  }
}

export class GetMenuOrganizationsFailure implements Action {
  readonly type = OrganizationsActionTypes.GetMenuOrganizationsFailure;

  constructor(public payload: any) {
  }
}

export class GetAffiliatedOrganizations implements Action {
  readonly type = OrganizationsActionTypes.GetAffiliatedOrganizations;
}

export class GetAffiliatedOrganizationsSuccess implements Action {
  readonly type = OrganizationsActionTypes.GetAffiliatedOrganizationsSuccess;

  constructor(public payload: Organization[]) {
  }
}

export class GetAffiliatedOrganizationsFailure implements Action {
  readonly type = OrganizationsActionTypes.GetAffiliatedOrganizationsFailure;

  constructor(public payload: any) {
  }
}

export class UpdateActiveOrganization implements Action {
  readonly type = OrganizationsActionTypes.UpdateActiveOrganization;

  constructor(public payload: string) {
  }
}

export class UpdateOrganization implements Action {
  readonly type = OrganizationsActionTypes.UpdateOrganization;

  constructor(public payload: Organization) {
  }
}

export class SetActiveOrganization implements Action {
  readonly type = OrganizationsActionTypes.SetActiveOrganization;

  constructor(public payload: string) {
  }
}

export class ChangeActiveOrganization implements Action {
  readonly type = OrganizationsActionTypes.ChangeActiveOrganization;

  constructor(public payload: string) {
  }
}

export type OrganizationsActionsUnion =
  | UpdateActiveOrganization
  | SetActiveOrganization
  | UpdateOrganization
  | GetMenuOrganizations
  | GetMenuOrganizationsSuccess
  | GetMenuOrganizationsFailure
  | GetAffiliatedOrganizations
  | GetAffiliatedOrganizationsSuccess
  | GetAffiliatedOrganizationsFailure
  | ChangeActiveOrganization;
