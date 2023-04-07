import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { Observable } from 'rxjs';
import { distinctUntilChanged, pluck, skip } from 'rxjs/operators';
import { combineLatest } from 'rxjs';
import { filter } from 'rxjs/operators';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { EmailTemplateTypes } from '../../core/models/email-template-types.enum';
import { EmailTemplatesService, OrganizationService, UserService } from '../../core/services';


@Component({
  selector: 'app-email-templates',
  templateUrl: './email-templates.component.html',
})

@DestroySubscribers()
export class EmailTemplatesComponent implements OnInit, OnDestroy, AddSubscribers {
  emailTemplates: { title: string; type: string }[];
  emailTemplateInviteColleagues: { title: string; type: string };
  emailTemplateRejectionNotification: { title: string; type: string };
  emailTemplateInvitePetitioner: { title: string; type: string };

  representativeIdFormControl: FormControl = this.organizationService.representativeIdControl;

  private updateTemplates$: Observable<[EmailTemplateTypes[], number]>;
  private subscribers: any = {};

  constructor(
    private emailTemplatesService: EmailTemplatesService,
    private route: ActivatedRoute,
    private userService: UserService,
    private organizationService: OrganizationService,
  ) {

  }

  ngOnInit() {

    this.emailTemplates = [
      {
        type: EmailTemplateTypes.NEW_CLIENT,
        title: 'TEMPLATE.ACCOUNT.EMAIL.COVER_LETTER_NEW',
      },
      {
        type: EmailTemplateTypes.UPDATED_CLIENT,
        title: 'TEMPLATE.ACCOUNT.EMAIL.COVER_LETTER_UPDATED',
      }
    ];


    this.emailTemplateInvitePetitioner = {
      type: EmailTemplateTypes.INVITE_APPLICANT,
      title: 'TEMPLATE.ACCOUNT.EMAIL.REGISTRATION_INVITATION',
    };
    this.emailTemplateInviteColleagues = {
      type: EmailTemplateTypes.INVITE_COLLEAGUE_TO_EASYVISA,
      title: 'TEMPLATE.ACCOUNT.EMAIL.REGISTRATION_INVITATION',
    };
    this.emailTemplateRejectionNotification = {
      type: EmailTemplateTypes.DOCUMENT_REJECTION_NOTIFICATION,
      title: 'TEMPLATE.ACCOUNT.EMAIL.REJECT_DOCUMENT',
    };

    this.updateTemplates$ = combineLatest([
      this.route.data.pipe(
        pluck('emailTemplateTypes'),
      ),
      this.organizationService.currentRepresentativeId$.pipe(
        filter((representativeId) => !!representativeId),
        distinctUntilChanged(),
      ),
    ]);
  }

  addSubscribers() {
    this.subscribers.updateTemplatesSubscription = this.updateTemplates$.pipe(
      skip(1)
    )
    .subscribe(
      ([emailTemplateTypes, id]) => this.emailTemplatesService.getEmailTemplates({
        templateType: emailTemplateTypes,
        representativeId: id.toString(),
      })
    );
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }
}
