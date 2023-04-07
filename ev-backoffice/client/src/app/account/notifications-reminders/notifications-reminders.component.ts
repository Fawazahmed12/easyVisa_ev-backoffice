import { Component, OnInit } from '@angular/core';

import { AttorneyNotificationsService } from '../services/attorney-notifications.service';


@Component({
  selector: 'app-notifications-reminders',
  templateUrl: './notifications-reminders.component.html',
})

export class NotificationsRemindersComponent implements OnInit {

  constructor(
    private attorneyNotificationsService: AttorneyNotificationsService,
  ) {
  }

  ngOnInit() {
    this.attorneyNotificationsService.getNotificationTypes();
  }
}
