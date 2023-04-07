import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { NotificationsRemindersRoutingModule } from './notifications-reminders-routing.module';
import { NotificationsRemindersComponent } from './notifications-reminders.component';
import {
  AttorneyRepresentativeNotificationsModule
} from './attorney-representative-notifications/attorney-representative-notifications.module';
import { EmailToClientModule } from './components/email-to-client/email-to-client.module';
import { ClientInactivityRemindersModule } from './client-inactivity-reminders/client-inactivity-reminders.module';
import { DeadlineRemindersModule } from './deadline-reminders/deadline-reminders.module';
import { RemindersService } from '../services/reminders.service';


@NgModule({
  imports: [
    SharedModule,
    NotificationsRemindersRoutingModule,
    AttorneyRepresentativeNotificationsModule,
    EmailToClientModule,
    ClientInactivityRemindersModule,
    DeadlineRemindersModule,
  ],
  providers: [
    RemindersService,
  ],
  declarations: [
    NotificationsRemindersComponent,
  ]
})
export class NotificationsRemindersModule {
}
