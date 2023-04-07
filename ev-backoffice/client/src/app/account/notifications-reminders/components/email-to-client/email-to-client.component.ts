import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';

import { merge, Subject } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { EmailTemplateTypes } from '../../../../core/models/email-template-types.enum';
import { EmailTemplatesService, OrganizationService } from '../../../../core/services';
import { EmailTemplate } from '../../../../core/models/email-template.model';
import {
  EmailTemplateVariablesModalComponent
} from '../../../../core/modals/email-template-variables-modal/email-template-variables-modal.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';


@Component({
  selector: 'app-email-to-client',
  templateUrl: './email-to-client.component.html',
})
@DestroySubscribers()
export class EmailToClientComponent implements OnInit, AddSubscribers, OnDestroy {
  @Input() title = 'TEMPLATE.ACCOUNT.NOTIFICATIONS_REMINDERS.CLIENT_INACTIVITY_REMINDERS.DEFAULT_TITLE_EMAIL_TO_CLIENT_CONTENT';
  @Input() contentFormControl: FormControl;
  @Input() subjectFormControl: FormControl = new FormControl('');
  @Input() emailTemplateType: EmailTemplateTypes;
  @Input() showSubject = false;

  loadDefaultEmailSubject$: Subject<void> = new Subject();
  loadDefaultEmailContent$: Subject<void> = new Subject();

  private subscribers: any = {};
  isChangeableContent = false;
  isChangeableSubject = false;


  constructor(
    private organizationService: OrganizationService,
    private emailTemplatesService: EmailTemplatesService,
    private ngbModal: NgbModal
  ) {
  }

  ngOnInit() {
    console.log(`${this.constructor.name} Initialized`);
  }

  addSubscribers() {
    this.subscribers.loadDefaultEmailSubscription = this.loadDefaultEmailContent$.pipe(
      switchMap(() => this.emailTemplatesService.getEmailTemplate({
          templateType: this.emailTemplateType,
          representativeId: this.organizationService.representativeIdControl.value,
        }, true)
      )
    ).subscribe((defaultTemplate: EmailTemplate) => {
        if (this.isChangeableContent) {
          this.contentFormControl.patchValue(defaultTemplate.content);
          this.isChangeableContent = false;
        }
      }
    );

    this.subscribers.loadDefaultEmailSubjectSubscription = this.loadDefaultEmailSubject$.pipe(
      switchMap(() => this.emailTemplatesService.getEmailTemplate({
          templateType: this.emailTemplateType,
          representativeId: this.organizationService.representativeIdControl.value,
        }, true)
      )
    ).subscribe((defaultTemplate: EmailTemplate) => {
        if (this.isChangeableSubject) {
          this.subjectFormControl.patchValue(defaultTemplate.subject);
          this.isChangeableSubject = false;
        }
      }
    );
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  loadDefaultEmailContent() {
    this.isChangeableContent = true;
    this.loadDefaultEmailContent$.next();
  }

  loadDefaultEmailSubject() {
    this.isChangeableSubject = true;
    this.loadDefaultEmailSubject$.next();
  }

  openEmailTemplateVariablesModal(){
    const modalRef = this.ngbModal.open(EmailTemplateVariablesModalComponent, {
      centered: true
    });
    modalRef.componentInstance.emailTemplateType = this.emailTemplateType;
  }
}
