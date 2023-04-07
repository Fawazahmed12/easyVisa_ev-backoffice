import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../shared/shared.module';
import { FindLabelPipeModule } from '../../../../../../shared/pipes/find-label/find-label-pipe.module';

import { BenefitCategoryComponent } from './benefit-category.component';

import { PetitionerNotSelectedModalComponent } from './modals/petitioner-not-selected-modal/petitioner-not-selected-modal.component';
import { PetitionerBenefitModalModule } from './modals/petitioner-benefit-modal/petitioner-benefit-modal.module';
import { ImmigrationBenefitModalModule } from './modals/immigration-benefit-modal/immigration-benefit-modal.module';
import { BenefitCategoryNamePipeModule } from '../../../../../../shared/pipes/benefit-category-name/benefit-category-name-pipe.module';

@NgModule({
  imports: [
    SharedModule,
    PetitionerBenefitModalModule,
    ImmigrationBenefitModalModule,
    FindLabelPipeModule,
    BenefitCategoryNamePipeModule,
  ],
  declarations: [
    BenefitCategoryComponent,
    PetitionerNotSelectedModalComponent,
  ],
  exports: [
    BenefitCategoryComponent,
  ]
})
export class BenefitCategoryModule {
}
