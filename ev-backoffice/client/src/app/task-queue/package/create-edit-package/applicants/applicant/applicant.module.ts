import { NgModule } from '@angular/core';


import { SharedModule } from '../../../../../shared/shared.module';
import { ApplicantInfoModule } from '../../../../components/applicant-info/applicant-info.module';

import { ApplicantComponent } from './applicant.component';
import {
  DeleteBeneficiaryApplicantModalComponent
} from './modals/delete-beneficiary-applicant-modal/delete-beneficiary-applicant-modal.component';
import { EmailAddressModule } from './email-address/email-address.module';
import { PetitionerStatusModule } from './petitioner-status/petitioner-status.module';
import { BenefitCategoryModule } from './benefit-category/benefit-category.module';
import {
  DeleteDerivativeBeneficiaryModalComponent
} from './modals/delete-derivative-beneficiary-modal/delete-derivative-beneficiary-modal.component';

@NgModule({
  imports: [
    SharedModule,
    ApplicantInfoModule,
    EmailAddressModule,
    BenefitCategoryModule,
    PetitionerStatusModule,
  ],
  declarations: [
    ApplicantComponent,
    DeleteBeneficiaryApplicantModalComponent,
    DeleteDerivativeBeneficiaryModalComponent,
  ],
  exports: [
    ApplicantComponent,
  ]
})
export class ApplicantModule {
}
