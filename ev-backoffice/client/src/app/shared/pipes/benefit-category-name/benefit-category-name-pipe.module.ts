import { NgModule } from '@angular/core';

import { BenefitCategoryNamePipe } from './benefit-category-name.pipe';

@NgModule({
  declarations: [
    BenefitCategoryNamePipe,
  ],
  exports: [
    BenefitCategoryNamePipe
  ]
})
export class BenefitCategoryNamePipeModule { }
