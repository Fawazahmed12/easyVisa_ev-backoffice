import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormArray, FormControl, FormGroup } from '@angular/forms';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { combineLatest, merge, Observable, scheduled, Subject } from 'rxjs';
import {
  filter,
  map,
  shareReplay,
  startWith,
  switchMap, tap,
  withLatestFrom
} from 'rxjs/operators';

import { isEqual } from 'lodash-es';

import { RequestState } from '../../../core/ngrx/utils';

import { NotificationSettings, UnitConfig } from '../../models/notification-settings.model';
import { AttorneyNotificationsService } from '../../services/attorney-notifications.service';
import { notificationConfig } from '../../models/notification-config';
import { OrganizationService } from '../../../core/services';


@Component({
  selector: 'app-attorney-representative-notifications',
  templateUrl: './attorney-representative-notifications.component.html',
})
@DestroySubscribers()
export class AttorneyRepresentativeNotificationsComponent implements OnInit, AddSubscribers, OnDestroy {
  taskQueueSettings$: Observable<UnitConfig[]>;
  clientProgressSettings$: Observable<UnitConfig[]>;
  notificationsConfigGetState$: Observable<RequestState<NotificationSettings>>;
  notificationsConfigPutState$: Observable<RequestState<NotificationSettings>>;
  saveChangesSubject$: Subject<NotificationSettings> = new Subject();
  cancelChangesSubject$: Subject<boolean> = new Subject();
  createAfterResetFormGroupSubject$: Subject<boolean> = new Subject();
  isEqual$: Observable<boolean>;
  currentRepresentativeUserId$: Observable<number>;

  formGroup: FormGroup;

  notificationConfig = notificationConfig;

  private subscribers: any = {};

  get isLoading$() {
    return merge(
      this.notificationsConfigGetState$.pipe(map((request) => request.loading)),
      this.notificationsConfigPutState$.pipe(map((request) => request.loading)),
    );
  }

  get taskQueueSettingsFormGroups() {
    return this.formGroup.get('taskQueue') as FormArray;
  }

  get clientProgressSettingsFormGroups() {
    return this.formGroup.get('clientProgress') as FormArray;
  }


  constructor(
    private attorneyNotificationsService: AttorneyNotificationsService,
    private organizationService: OrganizationService,
  ) {
    this.createFormGroup();
  }

  ngOnInit() {
    this.currentRepresentativeUserId$ = this.organizationService.currentRepresentativeUserId$;
    this.taskQueueSettings$ = this.attorneyNotificationsService.taskQueueSettings$;
    this.clientProgressSettings$ = this.attorneyNotificationsService.clientProgressSettings$;
    this.notificationsConfigGetState$ = this.attorneyNotificationsService.notificationsConfigGetState$;
    this.notificationsConfigPutState$ = this.attorneyNotificationsService.notificationsConfigPutState$;

    this.isEqual$ = combineLatest([
      this.formGroup.valueChanges.pipe(startWith('')),
      this.saveChangesSubject$.pipe(startWith('')),
      this.cancelChangesSubject$.pipe(startWith(''))
    ]).pipe(
        switchMap(() => combineLatest([
        this.taskQueueSettings$.pipe(
          map(taskQueueSettings => isEqual(taskQueueSettings, this.taskQueueSettingsFormGroups.value))
        ),
        this.clientProgressSettings$.pipe(
          map(clientProgressSettings => isEqual(clientProgressSettings, this.clientProgressSettingsFormGroups.value))),
      ])),
      map(([isEqualTaskQueue, isEqualClients]) => isEqualTaskQueue && isEqualClients),
      shareReplay(1)
    );
  }

  addSubscribers() {
    this.subscribers.currentRepresentativeUserIdSubscription = this.currentRepresentativeUserId$.pipe(
      filter(id => !!id),
    ).subscribe(id => this.attorneyNotificationsService.getNotificationsConfig(id));

    this.subscribers.taskQueueSettingsSubscription = this.taskQueueSettings$.pipe(
      filter(taskQueueSettings => !!taskQueueSettings),
    ).subscribe(taskQueueSettings => {
      while (0 !== this.taskQueueSettingsFormGroups.controls.length) {
        this.taskQueueSettingsFormGroups.removeAt(0);
      }
      taskQueueSettings.forEach( taskQueueSetting => this.taskQueueSettingsFormGroups.push(this.createUnitFormGroup(taskQueueSetting)));
    });

    this.subscribers.clientProgressSettingsSubscription = this.clientProgressSettings$.pipe(
      filter(clientProgressSettings => !!clientProgressSettings),
    ).subscribe(clientProgressSettings => {
      while (0 !== this.clientProgressSettingsFormGroups.controls.length) {
        this.clientProgressSettingsFormGroups.removeAt(0);
      }
      clientProgressSettings.forEach( clientProgressSetting => this.clientProgressSettingsFormGroups.push(
        this.createUnitFormGroup(clientProgressSetting)
      ));
    });

    this.subscribers.saveChangesSubscription = combineLatest([
      this.saveChangesSubject$,
      this.currentRepresentativeUserId$,
      this.organizationService.activeOrganizationId$
    ]).pipe(
      filter(([formGroupValue]) => !!formGroupValue)
    ).subscribe((([formGroupValue, id, activeOrganizationId]) => this.attorneyNotificationsService.putNotificationsConfig({
      config: formGroupValue,
      id,
      activeOrganizationId
    })))

    this.subscribers.resetChangesSubscription = this.cancelChangesSubject$.pipe(
      filter(cancelChanges => cancelChanges),
    )
    .subscribe(() => this.resetFormGroups());

    this.subscribers.createFormAfterResetSubscription = this.createAfterResetFormGroupSubject$.pipe(
      filter((createAfterResetForm) => createAfterResetForm),
      withLatestFrom(
        this.taskQueueSettings$,
        this.clientProgressSettings$
      ),
    ).subscribe(([, taskQueueSettings, clientProgressSettings]) => {
      this.cancelChangesSubject$.next(false);
      while (0 !== this.clientProgressSettingsFormGroups.controls.length) {
        this.clientProgressSettingsFormGroups.removeAt(0);
      }
      while (0 !== this.taskQueueSettingsFormGroups.controls.length) {
        this.taskQueueSettingsFormGroups.removeAt(0);
      }
      taskQueueSettings.forEach(
        taskQueueSetting => this.taskQueueSettingsFormGroups.push(this.createUnitFormGroup(taskQueueSetting)));
      clientProgressSettings.forEach(
        clientProgressSetting => this.clientProgressSettingsFormGroups.push(this.createUnitFormGroup(clientProgressSetting)));
    });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  createFormGroup() {
    this.formGroup = new FormGroup({
      taskQueue: new FormArray([]),
      clientProgress: new FormArray([]),
    });
  }

  createUnitFormGroup(data: UnitConfig) {
    return new FormGroup({
        type: new FormControl(data.type),
        preference: new FormControl(data.preference),
        id: new FormControl(data.id),
      }
    );
  }

  saveChanges() {
    this.saveChangesSubject$.next(this.formGroup.value);
  }

  cancelChanges() {
    this.cancelChangesSubject$.next(true);
  }

  resetFormGroups() {
    this.createAfterResetFormGroupSubject$.next(true);
  }
}
