import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import {
  NgbDateAdapter,
  NgbDateParserFormatter,
  NgbDatepickerModule,
} from '@ng-bootstrap/ng-bootstrap';

import { DatepickerGroupComponent } from './datepicker-group.component';
import { NgbDateISOAdapter } from './datepicker-adapter.service';
import { NgbDateParserFormatterService } from './datepicker-parser-formatter.service';

@NgModule({
  imports: [
    SharedModule,
    NgbDatepickerModule,
  ],
  declarations: [
    DatepickerGroupComponent,
  ],
  exports: [
    DatepickerGroupComponent,
  ],
  providers: [
    {provide: NgbDateAdapter, useClass: NgbDateISOAdapter},
    {provide: NgbDateParserFormatter, useClass: NgbDateParserFormatterService},
  ]
})

export class DatepickerGroupModule {
}
