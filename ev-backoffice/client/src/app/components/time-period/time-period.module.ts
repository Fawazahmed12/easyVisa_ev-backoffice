import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { DatepickerGroupModule } from '../datepicker-group/datepicker-group.module';

import { TimePeriodComponent } from './time-period.component';


@NgModule({
  imports: [
    SharedModule,
    DatepickerGroupModule,
  ],
  declarations: [
    TimePeriodComponent,
  ],
  exports: [
    TimePeriodComponent,
  ]
})
export class TimePeriodModule {}
