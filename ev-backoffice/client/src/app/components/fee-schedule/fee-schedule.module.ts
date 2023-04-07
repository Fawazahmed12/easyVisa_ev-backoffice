import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { FindLabelPipeModule } from '../../shared/pipes/find-label/find-label-pipe.module';
import { FeeScheduleComponent } from './fee-schedule.component';
import { FeeScheduleService } from '../../core/services';
import { MaskModule } from '../../shared/modules/mask.module';
import { NgxCurrencyModule } from 'ngx-currency';


@NgModule({
  imports: [
    SharedModule,
    FindLabelPipeModule,
    MaskModule,
    NgxCurrencyModule,
  ],
  declarations: [
    FeeScheduleComponent,
  ],
  exports: [
    FeeScheduleComponent,
  ],
  providers: [
    FeeScheduleService
  ]
})
export class FeeScheduleModule {}
