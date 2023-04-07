import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { filter, map, shareReplay, startWith, switchMap, withLatestFrom } from 'rxjs/operators';
import { Dictionary } from '@ngrx/entity';
import { combineLatest, Observable, Subject } from 'rxjs';

import { isEmpty, isEqual } from 'lodash-es';

import { OrganizationService } from '../../../core/services';
import { EmailTemplateTypes } from '../../../core/models/email-template-types.enum';
import { RequestState } from '../../../core/ngrx/utils';

import { RemindersService } from '../../services/reminders.service';
import { Reminder } from '../../models/reminder.model';


@Component({
  selector: 'app-client-inactivity-reminders',
  templateUrl: './client-inactivity-reminders.component.html',
})
@DestroySubscribers()
export class ClientInactivityRemindersComponent implements OnInit, AddSubscribers, OnDestroy {
  reminders$: Observable<Reminder[]>;
  remindersEntities$: Observable<Dictionary<Reminder>>;
  remindersGetState$: Observable<RequestState<Reminder[]>>;
  saveChangesSubject$: Subject<void> = new Subject();
  cancelChangesSubject$: Subject<boolean> = new Subject();
  isEqualQuestionnaireSection$: Observable<boolean>;
  isEqualDocumentsSection$: Observable<boolean>;
  isEqualData$: Observable<boolean>;
  currentRepIdOrgId$: Observable<number[]>;

  private subscribers: any = {};

  formGroup: FormGroup;

  selectorOptions: { value: number; label: string }[] = [
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
  ];

  get questionnaireRemindersFormGroup() {
    return this.formGroup.get('questionnaireReminders');
  }

  get documentPortalRemindersFormGroup() {
    return this.formGroup.get('documentPortalReminders');
  }

  constructor(
    private remindersService: RemindersService,
    private organizationService: OrganizationService,
  ) {
    this.createFormGroup();
  }

  ngOnInit() {
    this.reminders$ = this.remindersService.reminders$;
    this.remindersEntities$ = this.remindersService.remindersEntities$;
    this.remindersGetState$ = this.remindersService.remindersGetState$;
    this.currentRepIdOrgId$ = this.organizationService.currentRepIdOrgId$;

    this.isEqualQuestionnaireSection$ = this.questionnaireRemindersFormGroup.valueChanges.pipe(
      startWith(this.questionnaireRemindersFormGroup.value),
      switchMap(() => this.remindersEntities$.pipe(
        map(entities => isEqual(entities[EmailTemplateTypes.QUESTIONNAIRE_INACTIVITY], this.questionnaireRemindersFormGroup.value)),
        ),
      )
    );

    this.isEqualDocumentsSection$ = this.documentPortalRemindersFormGroup.valueChanges.pipe(
      startWith(this.questionnaireRemindersFormGroup.value),
      switchMap(() => this.remindersEntities$.pipe(
        map(entities => isEqual(entities[EmailTemplateTypes.DOCUMENT_PORTAL_INACTIVITY], this.documentPortalRemindersFormGroup.value)),
        )
      )
    );

    this.isEqualData$ = combineLatest([
      this.isEqualQuestionnaireSection$,
      this.isEqualDocumentsSection$,
    ]).pipe(
      map(([isEqualQuestionnaireSection, sEqualDocumentsSection]) => isEqualQuestionnaireSection && sEqualDocumentsSection),
      shareReplay(1)
    );
  }

  addSubscribers() {
    this.subscribers.currentRepIdSubscription = this.currentRepIdOrgId$.pipe(
      map(([repId, ]) => repId),
      filter(repId => !!repId),
    ).subscribe(repId => this.remindersService.getReminders(repId));

    this.subscribers.remindersEntitiesSubscription = combineLatest([
      this.remindersEntities$,
      this.cancelChangesSubject$.pipe(
        startWith(true),
      )
    ]).pipe(
      filter(([entities, ]) => !isEmpty(entities)),
    ).subscribe(([entities, ]) => {
      const value = {
        questionnaireReminders: entities[EmailTemplateTypes.QUESTIONNAIRE_INACTIVITY],
        documentPortalReminders: entities[EmailTemplateTypes.DOCUMENT_PORTAL_INACTIVITY]
      };
      this.formGroup.patchValue(value);
    });

    this.subscribers.saveChangesSubscription = combineLatest([
      this.saveChangesSubject$,
      this.organizationService.activeOrganizationId$]).pipe(
      withLatestFrom(this.currentRepIdOrgId$)
    ).subscribe(([[, activeOrganizationId], [repId]]) =>
      this.remindersService.patchReminders(
        {
          id: repId,
          reminders: [
            this.questionnaireRemindersFormGroup.value,
            this.documentPortalRemindersFormGroup.value,
          ],
          activeOrganizationId
        }
      ));
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  createFormGroup() {
    this.formGroup = new FormGroup({
      questionnaireReminders: new FormGroup({
        id: new FormControl(),
        content: new FormControl(),
        repeatInterval: new FormControl(),
        subject: new FormControl(),
        templateType: new FormControl(),
      }),
      documentPortalReminders: new FormGroup({
        id: new FormControl(),
        content: new FormControl(),
        repeatInterval: new FormControl(),
        subject: new FormControl(),
        templateType: new FormControl(),
      })
    });
  }

  saveChanges() {
    this.saveChangesSubject$.next();
  }

  cancelChanges() {
    this.cancelChangesSubject$.next();
  }
}
