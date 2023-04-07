import { Action } from '@ngrx/store';

import { PackageApplicant } from '../../../core/models/package/package-applicant.model';

import { NON_REGISTERED_APPLICANTS } from './non-registered-applicants.state';


export const NonRegisteredApplicantsActionTypes = {
  DeleteNonRegisteredApplicant: `[${NON_REGISTERED_APPLICANTS}] Delete Non-Registered Applicant`,
  DeleteNonRegisteredApplicantSuccess: `[${NON_REGISTERED_APPLICANTS}] Delete Non-Registered Applicant Success`,
  DeleteNonRegisteredApplicantFailure: `[${NON_REGISTERED_APPLICANTS}] Delete Non-Registered Applicant Failure`,
  SetNonRegisteredApplicants: `[${NON_REGISTERED_APPLICANTS}] Set Non Registered Applicants`,
};


export class SetNonRegisteredApplicants implements Action {
  readonly type = NonRegisteredApplicantsActionTypes.SetNonRegisteredApplicants;

  constructor(public payload: PackageApplicant[]) {
  }
}

export class DeleteNonRegisteredApplicant implements Action {
  readonly type = NonRegisteredApplicantsActionTypes.DeleteNonRegisteredApplicant;

  constructor(public payload: {id: number}) {
  }
}

export class DeleteNonRegisteredApplicantSuccess implements Action {
  readonly type = NonRegisteredApplicantsActionTypes.DeleteNonRegisteredApplicantSuccess;

  constructor(public payload: {id: number}) {
  }
}

export class DeleteNonRegisteredApplicantFailure implements Action {
  readonly type = NonRegisteredApplicantsActionTypes.DeleteNonRegisteredApplicantFailure;

  constructor(public payload?: any) {
  }
}

export type NonRegisteredApplicantsActionsUnion =
  | SetNonRegisteredApplicants
  | DeleteNonRegisteredApplicant
  | DeleteNonRegisteredApplicantSuccess
  | DeleteNonRegisteredApplicantFailure;
