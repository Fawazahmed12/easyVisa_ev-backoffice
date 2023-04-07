import { Pipe, PipeTransform } from '@angular/core';

import { Benefits } from '../../../core/models/benefits.model';
import { PetitionerStatus } from '../../../core/models/petitioner-status.enum';
import { ApplicantType } from '../../../core/models/applicantType.enum';
import { BenefitCategories } from '../../../core/models/benefit-categories.enum';

import { isNull } from 'lodash-es';

@Pipe({
  name: 'benefitCategoryName'
})
export class BenefitCategoryNamePipe implements PipeTransform {

  transform(
    value: string,
    citizenshipStatus: string,
    benefits: Benefits,
    applicantTypeFormValue: ApplicantType,
    index: number,
    secondApplicantBenefitCategory: BenefitCategories,
    findFullLabel = true,
  ): string {
    const commonWarningMessage = 'TEMPLATE.TASK_QUEUE.APPLICANT.BENEFIT_WARNING_WRONG_SELECT';
    const selectCategoryWarningMessage = 'TEMPLATE.TASK_QUEUE.APPLICANT.BENEFIT_WARNING_NOT_SELECT';
    const petitionerLabel = 'TEMPLATE.TASK_QUEUE.APPLICANT.PETITIONER';
    const petitionerDescription = 'TEMPLATE.TASK_QUEUE.PETITIONER_BENEFIT_CATEGORY_MODAL.PETITIONER_TIPS_2';
    const beneficiaries: ApplicantType[] = [ApplicantType.BENEFICIARY, ApplicantType.PRINCIPAL_BENEFICIARY];
    const alienBenefitCategories = ['SIX01', 'SIX01A', 'EAD'];

    let updatedValue = value;

    // TODO hardcoded functionality for disabling der beneficiary categories
    function checkDerivativeCategory() {
      if (secondApplicantBenefitCategory === BenefitCategories.K1K3) {
        updatedValue = value !== BenefitCategories.K2K4 ? commonWarningMessage : updatedValue;
      } else {
        updatedValue = value !== secondApplicantBenefitCategory ? commonWarningMessage : updatedValue;
      }
    }

    if (value === '' && citizenshipStatus && findFullLabel) {
      return selectCategoryWarningMessage;
    } else if (isNull(value) && citizenshipStatus && findFullLabel) {
      return petitionerDescription;
    } else if (isNull(value) && citizenshipStatus) {
      return petitionerLabel;
    } else if (!value) {
      return null;
    }

    switch (citizenshipStatus) {
      case PetitionerStatus.U_S_CITIZEN: {
        if (beneficiaries.includes(applicantTypeFormValue)) {
          updatedValue = benefits.disabledUSCitizenCategories.includes(value) ? commonWarningMessage : value;
        } else if (applicantTypeFormValue === ApplicantType.DERIVATIVE_BENEFICIARY) {
          updatedValue = benefits.disabledUSDerivativeCategories.includes(value) ? commonWarningMessage : value;
          checkDerivativeCategory();
        }
      }
        break;
      case PetitionerStatus.LPR: {
        if (beneficiaries.includes(applicantTypeFormValue)) {
          updatedValue = benefits.disabledLPRCategories.includes(value) ? commonWarningMessage : value;
        } else if (applicantTypeFormValue === ApplicantType.DERIVATIVE_BENEFICIARY) {
          updatedValue = benefits.disabledLPRDerivativeCategories.includes(value) ? commonWarningMessage : value;
          checkDerivativeCategory();
        }
      }
        break;
      case PetitionerStatus.ALIEN: {
        if (!alienBenefitCategories.includes(value)) {
          updatedValue = commonWarningMessage;
        }
      }
    }

    if (findFullLabel) {
      const foundedItem = benefits.benefitCategories.find((item) => item.value === updatedValue);
      updatedValue = foundedItem ? foundedItem.fullLabel : updatedValue;
    } else {
      const foundedItem = benefits.benefitCategories.find((item) => item.value === value);
      updatedValue = updatedValue !== value ? `<span class="text-danger">${foundedItem.label}</span>` : foundedItem.label;
    }

    return updatedValue;
  }
}
