import { Component, Input, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';

import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { AttorneyNotificationsService } from '../../../services/attorney-notifications.service';


@Component({
  selector: 'app-special-logic',
  templateUrl: './special-logic.component.html',
})

export class SpecialLogicComponent implements OnInit {
  @Input() finalInterval = false;
  @Input() repeatIntervalFormControl: FormControl;
  @Input() daysAfterFormControl: FormControl;
  @Input() title: string;
  @Input() mainTitle = 'TEMPLATE.ACCOUNT.NOTIFICATIONS_REMINDERS.DEADLINE_REMINDERS.TRIGGER_AND_REPEATING';
  @Input() isPaymentSection = false;

  isActiveNotificationImportantDocument$: Observable<boolean>;
  selectorOptions$: Observable<{ value: number; label: string }[]>;


  constructor(
    private attorneyNotificationsService: AttorneyNotificationsService,
  ) {
  }

  ngOnInit() {
    this.isActiveNotificationImportantDocument$ = this.attorneyNotificationsService.isActiveNotificationImportantDocument$;
    this.selectorOptions$ = this.attorneyNotificationsService.isActiveNotificationImportantDocument$.pipe(
      map(isActiveNotificationImportantDocument => !isActiveNotificationImportantDocument || this.isPaymentSection ? [
        {
          value: 0,
          label: 'FORM.LABELS.OFF',
        },
        {
          value: 7,
          label: '7',
        },
        {
          value: 14,
          label: '14',
        },
        {
          value: 30,
          label: '30',
        },
      ] : [
        {
          value: 0,
          label: 'FORM.LABELS.OFF',
        },
        {
          value: 1,
          label: 'TEMPLATE.ACCOUNT.NOTIFICATIONS_REMINDERS.DEADLINE_REMINDERS.SEND_EMAIL_ONCE_ONLY',
        },
      ])
    );
  }
}
