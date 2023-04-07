import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../../../shared/shared.module';

import { SearchBenefitCategoriesComponent } from './search-benefit-categories.component';

@NgModule({
  imports: [
    SharedModule
  ],
  declarations: [SearchBenefitCategoriesComponent],
  exports: [SearchBenefitCategoriesComponent]
})
export class SearchBenefitCategoriesModule { }
