import { NgModule } from '@angular/core';

import { BlockModule } from '../../../../shared/components/block/block.module';
import { SharedModule } from '../../../../shared/shared.module';
import { SpinnerModule } from '../../../../components/spinner/spinner.module';

import { AttorneyNotificationsService } from '../../../services/attorney-notifications.service';

import { SpecialLogicComponent } from './special-logic.component';


@NgModule({
  imports: [
    BlockModule,
    SharedModule,
    SpinnerModule,
  ],
  declarations: [
    SpecialLogicComponent,
  ],
  exports: [
    SpecialLogicComponent,
  ],
  providers: [
    AttorneyNotificationsService
  ]
})

export class SpecialLogicModule {
}
