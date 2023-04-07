import { NgModule } from '@angular/core';

import { NgbAccordionModule } from '@ng-bootstrap/ng-bootstrap';

import { SharedModule } from '../../shared/shared.module';
import { DatepickerGroupModule } from '../../components/datepicker-group/datepicker-group.module';

import { MilestoneDatesComponent } from './milestone-dates.component';


@NgModule({
  imports: [
    SharedModule,
    NgbAccordionModule,
    DatepickerGroupModule,
  ],
  declarations: [
    MilestoneDatesComponent,
  ],
  exports: [
    MilestoneDatesComponent,
  ]
})
export class MilestoneDatesModule { }
