import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../shared/shared.module';
import { DatepickerGroupModule } from '../../../../../../components/datepicker-group/datepicker-group.module';
import { TimePeriodModule } from '../../../../../../components/time-period/time-period.module';

import { BeneficiaryCountriesModalModule } from '../../beneficiary-countries-modal/beneficiary-countries-modal.module';
import { MultipleClientSearchComponent } from './multiple-client-search.component';
import { SearchFilterComponent } from './components/search-filter-array/search-filter.component';
import { SearchFilterElementComponent } from './components/search-filter-element/search-filter-element.component';
import { FindLabelPipeModule } from '../../../../../../shared/pipes/find-label/find-label-pipe.module';
import { SearchBenefitCategoriesModule } from './components/search-benefit-categories/search-benefit-categories.module';

@NgModule({
  imports: [
    SharedModule,
    DatepickerGroupModule,
    BeneficiaryCountriesModalModule,
    FindLabelPipeModule,
    TimePeriodModule,
    SearchBenefitCategoriesModule,
  ],
  declarations: [
    MultipleClientSearchComponent,
    SearchFilterComponent,
    SearchFilterElementComponent,
  ],
  exports: [
    MultipleClientSearchComponent,
  ]
})

export class MultipleClientSearchModule {
}
