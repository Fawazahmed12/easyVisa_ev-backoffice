import { createFeatureSelector, createSelector } from '@ngrx/store';
import { BlankForm, FormsPackageApplicant, PackageContinuationSheets, PackageForm } from '../../models/forms-sheets.model';

export const FORMS_SHEETS = 'FormsSheets';

export interface FormsSheetsState {
  packageApplicants: FormsPackageApplicant[];
  packageForms: PackageForm[];
  packageContinuationSheets: PackageContinuationSheets[];
  selectedApplicantsIds: number[];
  blanks: BlankForm[];
}

export const selectFormsSheetsState = createFeatureSelector<FormsSheetsState>(FORMS_SHEETS);

export const selectPackageApplicants = ({packageApplicants}: FormsSheetsState) => packageApplicants;
export const selectPackageForms = ({packageForms}: FormsSheetsState) => packageForms;
export const selectPackageContinuationSheets = ({packageContinuationSheets}: FormsSheetsState) => packageContinuationSheets;
export const selectSelectedApplicantsIds = ({selectedApplicantsIds}: FormsSheetsState) => selectedApplicantsIds;
export const selectBlanks = ({blanks}: FormsSheetsState) => blanks;

export const selectSelectedApplicants = createSelector(
  selectSelectedApplicantsIds,
  selectPackageApplicants,
  (selectedApplicantsIds, applicants) => {
    if (selectedApplicantsIds) {
      return applicants.filter((applicant) => selectedApplicantsIds.includes(applicant.applicantId));
    } else {
      return null;
    }
  }
);


