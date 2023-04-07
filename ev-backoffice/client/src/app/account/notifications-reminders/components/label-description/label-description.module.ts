import { NgModule } from '@angular/core';

import { BlockModule } from '../../../../shared/components/block/block.module';
import { SharedModule } from '../../../../shared/shared.module';
import { SpinnerModule } from '../../../../components/spinner/spinner.module';
import { FindLabelPipeModule } from '../../../../shared/pipes/find-label/find-label-pipe.module';

import { AttorneyNotificationsService } from '../../../services/attorney-notifications.service';
import { LabelDescriptionComponent } from './label-description.component';



@NgModule({
  imports: [
    BlockModule,
    SharedModule,
    SpinnerModule,
    FindLabelPipeModule
  ],
  declarations: [
    LabelDescriptionComponent,
  ],
  exports: [
    LabelDescriptionComponent,
  ],
  providers: [
    AttorneyNotificationsService
  ]
})

export class LabelDescriptionModule {
}
