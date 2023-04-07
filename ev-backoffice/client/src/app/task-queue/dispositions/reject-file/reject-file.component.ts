import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { filter, map, withLatestFrom } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { EmailTemplatesService, OrganizationService } from '../../../core/services';
import { EmailTemplate } from '../../../core/models/email-template.model';
import { EmailTemplateTypes } from '../../../core/models/email-template-types.enum';
import { RequestState } from '../../../core/ngrx/utils';
import { noWhitespaceValidator } from '../../../auth/validators/no-white-space.validator';
import { DispositionsService } from '../dispositions.service';
import { Disposition } from '../../models/dispositions.model';
import { Organization } from '../../../core/models/organization.model';
import { Subject } from 'rxjs';
import {
  EmailTemplateVariablesModalComponent
} from '../../../core/modals/email-template-variables-modal/email-template-variables-modal.component';


@Component({
  selector: 'app-reject-file',
  templateUrl: './reject-file.component.html',
})
@DestroySubscribers()
export class RejectFileComponent implements AddSubscribers, OnDestroy, OnInit {
  @Input() file;
  activeOrganization$: Observable<Organization>;
  activeDisposition$: Observable<Disposition>;
  emailTemplate$: Observable<EmailTemplate>;
  getEmailTemplateRequest$: Observable<RequestState<EmailTemplate>>;
  sendEmailDispositionSubject$: Subject<boolean> = new Subject<boolean>();

  formGroup: FormGroup;

  private subscribers: any = {};

  get contentFormControl() {
    return this.formGroup.get('content');
  }

  get subjectFormControl() {
    return this.formGroup.get('subject');
  }

  constructor(
    private emailTemplatesService: EmailTemplatesService,
    private dispositionsService: DispositionsService,
    private organizationService: OrganizationService,
    private activeModal: NgbActiveModal,
    private ngbModal: NgbModal
  ) {
    this.createFormGroup();
  }

  ngOnInit() {
    this.activeOrganization$ = this.organizationService.activeOrganization$;
    this.activeDisposition$ = this.dispositionsService.activeDisposition$;
    this.getEmailTemplateRequest$ = this.emailTemplatesService.getEmailTemplateRequest$;
    this.emailTemplate$ = this.emailTemplatesService.emailTemplatesEntities$.pipe(
      map((entities) => entities[EmailTemplateTypes.DOCUMENT_REJECTION_NOTIFICATION]),
    );
  }

  addSubscribers() {
    this.subscribers.emailTemplateSubscription = this.emailTemplate$.pipe(
      filter((emailTemplate) => !!emailTemplate),
    ).subscribe((emailTemplate) => this.formGroup.patchValue(emailTemplate));

    this.subscribers.sendEmailSubscription = this.sendEmailDispositionSubject$.pipe(
      withLatestFrom(this.activeOrganization$),
      filter(([ , organization ]) => !!organization),
    ).subscribe(([ , organization ]) => this.activeModal.close({
        emailTemplate: this.contentFormControl.value,
        subject: this.subjectFormControl.value,
        organizationId: organization.id
      }));
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  createFormGroup() {
    this.formGroup = new FormGroup({
      content: new FormControl('', [Validators.required, noWhitespaceValidator]),
      subject: new FormControl('', [Validators.required, noWhitespaceValidator]),
    });
  }

  closeModal() {
    this.activeModal.dismiss();
  }

  saveEmail() {
    this.sendEmailDispositionSubject$.next(true);
  }

  openEmailTemplateVariablesModal() {
    const modalRef = this.ngbModal.open(EmailTemplateVariablesModalComponent, {
      centered: true
    });
    modalRef.componentInstance.emailTemplateType = EmailTemplateTypes.DOCUMENT_REJECTION_NOTIFICATION;
  }
}
