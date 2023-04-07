import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';

import { TimeFrameComponent } from './time-frame.component';


@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [
    TimeFrameComponent
  ],
  exports: [
    TimeFrameComponent
  ]
})
export class TimeFrameModule {
}
