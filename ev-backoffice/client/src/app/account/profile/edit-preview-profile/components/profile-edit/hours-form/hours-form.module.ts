import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../shared/shared.module';
import { FindLabelPipeModule } from '../../../../../../shared/pipes/find-label/find-label-pipe.module';

import { HoursFormComponent } from './hours-form.component';
import { TimeSelectorModule } from './time-selector/time-selector.module';

@NgModule({
  imports: [
    SharedModule,
    FindLabelPipeModule,
    TimeSelectorModule,
  ],
  declarations: [
    HoursFormComponent,
  ],
  exports: [
    HoursFormComponent,
  ]
})

export class HoursFormModule {
}
