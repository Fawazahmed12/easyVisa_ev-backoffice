import { NgModule } from '@angular/core';

import { SpinnerModule } from '../../../components/spinner/spinner.module';
import { BlockModule } from '../../../shared/components/block/block.module';
import { SharedModule } from '../../../shared/shared.module';
import { FindLabelPipeModule } from '../../../shared/pipes/find-label/find-label-pipe.module';

import { RemindersService } from '../../services/reminders.service';

import { EmailToClientModule } from '../components/email-to-client/email-to-client.module';
import { DeadlineRemindersComponent } from './deadline-reminders.component';
import { LabelDescriptionModule } from '../components/label-description/label-description.module';
import { SpecialLogicModule } from '../components/special-logic/special-logic.module';


@NgModule({
  imports: [
    BlockModule,
    SharedModule,
    SpinnerModule,
    FindLabelPipeModule,
    EmailToClientModule,
    LabelDescriptionModule,
    SpecialLogicModule
  ],
  declarations: [
    DeadlineRemindersComponent,
  ],
  exports: [
    DeadlineRemindersComponent,
  ]
})

export class DeadlineRemindersModule {
}
