import { NgModule } from '@angular/core';

import { BlockModule } from '../../../../shared/components/block/block.module';
import { SharedModule } from '../../../../shared/shared.module';
import { SpinnerModule } from '../../../../components/spinner/spinner.module';
import { FindLabelPipeModule } from '../../../../shared/pipes/find-label/find-label-pipe.module';

import { AttorneyNotificationsService } from '../../../services/attorney-notifications.service';

import { EmailToClientComponent } from './email-to-client.component';


@NgModule({
  imports: [
    BlockModule,
    SharedModule,
    SpinnerModule,
    FindLabelPipeModule
  ],
  declarations: [
    EmailToClientComponent,
  ],
  exports: [
    EmailToClientComponent,
  ],
  providers: [
    AttorneyNotificationsService
  ]
})

export class EmailToClientModule {
}
