import { NgModule } from '@angular/core';

import { SpinnerModule } from '../../../components/spinner/spinner.module';
import { BlockModule } from '../../../shared/components/block/block.module';
import { SharedModule } from '../../../shared/shared.module';
import { FindLabelPipeModule } from '../../../shared/pipes/find-label/find-label-pipe.module';

import { EmailToClientModule } from '../components/email-to-client/email-to-client.module';

import { ClientInactivityRemindersComponent } from './client-inactivity-reminders.component';


@NgModule({
  imports: [
    BlockModule,
    SharedModule,
    SpinnerModule,
    FindLabelPipeModule,
    EmailToClientModule
  ],
  declarations: [
    ClientInactivityRemindersComponent,
  ],
  exports: [
    ClientInactivityRemindersComponent,
  ]
})

export class ClientInactivityRemindersModule {
}
