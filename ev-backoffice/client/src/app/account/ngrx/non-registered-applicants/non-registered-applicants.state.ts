import { createFeatureSelector } from '@ngrx/store';

import { PackageApplicant } from '../../../core/models/package/package-applicant.model';

export const NON_REGISTERED_APPLICANTS = 'NonRegisteredApplicants';

export interface NonRegisteredApplicantsState {
  nonRegisteredApplicants: PackageApplicant[];
}

export const selectNonRegisteredApplicantState = createFeatureSelector<NonRegisteredApplicantsState>(NON_REGISTERED_APPLICANTS);

export const selectNonRegisteredApplicants = ({nonRegisteredApplicants}: NonRegisteredApplicantsState) => nonRegisteredApplicants;

