import { NgModule } from '@angular/core';

import { NumberOfYearsPipe } from './number-of-years.pipe';

@NgModule({
  declarations: [
    NumberOfYearsPipe,
  ],
  exports: [
    NumberOfYearsPipe
  ]
})
export class NumberOfYearsPipeModule {
}
