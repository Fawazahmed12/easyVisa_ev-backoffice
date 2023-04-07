import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, map, shareReplay, startWith } from 'rxjs/operators';

import { isEqual } from 'lodash-es';

import { RequestState } from '../../../core/ngrx/utils';
import { OrganizationService } from '../../../core/services';

import { NotificationCategory, NotificationTypes } from '../../models/notyfication-types.model';
import { AttorneyNotificationsService } from '../../services/attorney-notifications.service';
import { RemindersService } from '../../services/reminders.service';
import { Reminder } from '../../models/reminder.model';


@Component({
  selector: 'app-deadline-reminders',
  templateUrl: './deadline-reminders.component.html',
})
@DestroySubscribers()
export class DeadlineRemindersComponent implements OnInit, AddSubscribers, OnDestroy {
  notificationTypes$: Observable<NotificationTypes>;
  notificationTypesGetState$: Observable<RequestState<NotificationTypes>>;
  activeDeadLineReminderItem$: Observable<NotificationCategory>;
  activeNotification$: Observable<NotificationCategory>;
  activeItem$: Observable<Reminder>;
  activeDeadlineReminder$: Observable<Reminder>;
  isEqualData$: Observable<boolean>;
  remindersGetState$: Observable<RequestState<Reminder[]>>;
  isLoading$: Observable<boolean>;

  private representativeIdControl: FormControl = this.organizationService.representativeIdControl;
  private saveChangesSubject$: Subject<void> = new Subject();
  private cancelChangesSubject$: Subject<boolean> = new Subject();
  private subscribers: any = {};

  formGroup: FormGroup;

  get contentFormControl() {
    return this.formGroup.get('content');
  }

  get subjectFormControl() {
    return this.formGroup.get('subject');
  }

  get repeatIntervalFormControl() {
    return this.formGroup.get('repeatInterval');
  }

  get formValue$() {
    return this.formGroup.valueChanges.pipe(
      startWith(this.formGroup.value),
      shareReplay(1),
    );
  }

  constructor(
    private attorneyNotificationsService: AttorneyNotificationsService,
    private remindersService: RemindersService,
    private organizationService: OrganizationService,
  ) {
  }

  ngOnInit() {
    this.notificationTypes$ = this.attorneyNotificationsService.notificationTypes$;
    this.notificationTypesGetState$ = this.attorneyNotificationsService.notificationTypesGetState$;
    this.activeDeadLineReminderItem$ = this.attorneyNotificationsService.activeDeadLineReminderItem$;
    this.activeNotification$ = this.attorneyNotificationsService.activeNotification$;
    this.activeDeadlineReminder$ = this.remindersService.activeDeadlineReminder$;
    this.activeItem$ = this.remindersService.activeItem$;
    this.remindersGetState$ = this.remindersService.remindersGetState$;

    this.isLoading$ = this.notificationTypesGetState$.pipe(
      map((notificationTypesGetState) =>
        notificationTypesGetState.loading &&
        !notificationTypesGetState.loaded
      ),
      shareReplay(1)
    );

    this.createFormGroup();

    this.isEqualData$ = combineLatest([
      this.formValue$,
      this.activeItem$
    ]).pipe(
      filter(([, initialValue]) => !!initialValue),
      map(([formGroup, initialValue]) => isEqual(formGroup, initialValue)),
      shareReplay(1)
    );
  }

  addSubscribers() {
    this.subscribers.forBuildFormGroupSubscription = combineLatest([
      this.cancelChangesSubject$.pipe(startWith(true)),
      this.activeItem$,
    ]).pipe(
      filter(([, activeDeadLineItemValue]: [boolean, Reminder]) => !!activeDeadLineItemValue),
    ).subscribe(([, activeDeadLineItemValue]) => this.formGroup.patchValue(activeDeadLineItemValue));

    this.subscribers.saveChangesSubscription =
      combineLatest([
        this.saveChangesSubject$,
        this.organizationService.activeOrganizationId$
      ])
        .subscribe(([,activeOrganizationId]) => this.remindersService.patchReminders(
      {
        id: this.representativeIdControl.value,
        reminders: [this.formGroup.value],
        activeOrganizationId
      }
    ));
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  createFormGroup(data = {
    content: null,
    subject: null,
    id: null,
    templateType: null,
    repeatInterval: null,
  }) {
    this.formGroup = new FormGroup({
      id: new FormControl(data.id),
      content: new FormControl(data.content),
      subject: new FormControl(data.subject),
      repeatInterval: new FormControl(data.repeatInterval),
      templateType: new FormControl(data.templateType),
    });
  }

  setActiveItem(value) {
    this.remindersService.setActiveReminderType(value);
  }

  saveChanges() {
    this.saveChangesSubject$.next();
  }

  cancelChanges() {
    this.cancelChangesSubject$.next();
  }
}
