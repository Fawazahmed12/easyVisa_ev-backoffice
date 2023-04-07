import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../../shared/shared.module';

import { TimeSelectorComponent } from './time-selector.component';

@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [
    TimeSelectorComponent,
  ],
  exports: [
    TimeSelectorComponent,
  ]
})

export class TimeSelectorModule {
}
