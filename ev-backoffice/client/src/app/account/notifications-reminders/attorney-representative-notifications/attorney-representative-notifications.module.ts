import { NgModule } from '@angular/core';

import { SpinnerModule } from '../../../components/spinner/spinner.module';
import { BlockModule } from '../../../shared/components/block/block.module';
import { SharedModule } from '../../../shared/shared.module';
import { FindLabelPipeModule } from '../../../shared/pipes/find-label/find-label-pipe.module';

import { AttorneyNotificationsService } from '../../services/attorney-notifications.service';

import { AttorneyRepresentativeNotificationsComponent } from './attorney-representative-notifications.component';


@NgModule({
  imports: [
    BlockModule,
    SharedModule,
    SpinnerModule,
    FindLabelPipeModule
  ],
  declarations: [
    AttorneyRepresentativeNotificationsComponent,
  ],
  exports: [
    AttorneyRepresentativeNotificationsComponent,
  ],
  providers: [
    AttorneyNotificationsService
  ]
})

export class AttorneyRepresentativeNotificationsModule {
}
