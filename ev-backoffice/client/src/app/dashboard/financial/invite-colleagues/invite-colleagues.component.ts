import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { catchError, filter, switchMap } from 'rxjs/operators';
import { EMPTY, Observable, Subject } from 'rxjs';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { EmailTemplatesService, ModalService, OrganizationService, PackagesService } from '../../../core/services';
import { EmailTemplateTypes } from '../../../core/models/email-template-types.enum';
import { RequestState } from '../../../core/ngrx/utils';
import { EmailTemplate } from '../../../core/models/email-template.model';
import { noWhitespaceValidator } from '../../../auth/validators/no-white-space.validator';
import { Email } from '../../../core/models/email.model';
import { FinancialService } from '../financial.service';
import {
  EmailTemplateVariablesModalComponent
} from '../../../core/modals/email-template-variables-modal/email-template-variables-modal.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';


@Component({
  selector: 'app-invite-colleagues',
  templateUrl: './invite-colleagues.component.html',
  styles: [ `
  .font-16{
    font-size: 16px;
  }
  ` ]
})
@DestroySubscribers()
export class InviteColleaguesComponent implements OnInit, AddSubscribers, OnDestroy {
  getEmailTemplateRequest$: Observable<RequestState<EmailTemplate>>;
  formGroup: FormGroup;
  private formSubmitSubject$: Subject<Email> = new Subject<Email>();
  private subscribers: any = {};

  constructor(
    private emailTemplatesService: EmailTemplatesService,
    private organizationService: OrganizationService,
    private modalService: ModalService,
    private packagesService: PackagesService,
    private financialService: FinancialService,
    private router: Router,
    private ngbModal: NgbModal
  ) {
    this.createFormGroup();
  }

  get emailsFormControl() {
    return this.formGroup.get('emails');
  }

  get subjectFormControl() {
    return this.formGroup.get('subject');
  }

  get contentFormControl() {
    return this.formGroup.get('content');
  }

  ngOnInit() {
    this.getEmailTemplateRequest$ = this.emailTemplatesService.getEmailTemplateRequest$;
  }

  addSubscribers() {
    this.subscribers.representativeIdSubscription = this.organizationService.currentRepresentativeId$.pipe(
      filter((currentRepresentativeId) => typeof currentRepresentativeId !== 'undefined'),
      switchMap((representativeId) => this.emailTemplatesService.getEmailTemplate(
        {
          templateType: EmailTemplateTypes.INVITE_COLLEAGUE_TO_EASYVISA,
          representativeId
        }).pipe(
        catchError((error: HttpErrorResponse) => {
            if (error.status !== 401) {
              this.modalService.showErrorModal(error.error.errors || [ error.error ]);
            }
            return EMPTY;
          }
        ),
      )),
      filter((res) => !!res),
    ).subscribe((emailTemplate) => this.formGroup.patchValue(emailTemplate));

    this.subscribers.formSubmitSubscription = this.formSubmitSubject$.pipe(
      filter(() => this.formGroup.valid),
      switchMap((data) => this.financialService.inviteColleagues(data).pipe(
          catchError((error: HttpErrorResponse) => {
              if (error.status !== 401) {
                this.modalService.showErrorModal(error.error.errors || [ error.error ]);
              }
              return EMPTY;
            }
          )
        ))
    ).subscribe((res: any) => {
      this.router.navigate([ 'dashboard', 'financial' ]);
    });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  createFormGroup() {
    this.formGroup = new FormGroup({
      templateType: new FormControl(null),
      emails: new FormControl('', [ Validators.required, noWhitespaceValidator ]),
      content: new FormControl('', [ Validators.required, noWhitespaceValidator ]),
      representativeId: new FormControl(),
      subject: new FormControl('', [ Validators.required, noWhitespaceValidator ]),
    });
  }

  sendEmail() {
    const emailData = this.formGroup.getRawValue();
    const emailList = emailData.emails.split(/,|;| /);
    const invalidEmails = [];
    emailList.forEach((email) => {
      const emailId = email.trim();
      if (!this.validateEmail(emailId)) {
        invalidEmails.push(emailId);
      }
    });
    if (invalidEmails.length) {
      this.modalService.showErrorModal('Invalid email address(es): ' + invalidEmails.join());
      return;
    }
    this.formSubmitSubject$.next(emailData);
  }

  validateEmail(email) {
    const re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
  }

  openEmailTemplateVariablesModal() {
    const modalRef = this.ngbModal.open(EmailTemplateVariablesModalComponent, {
      centered: true
    });
    modalRef.componentInstance.emailTemplateType = EmailTemplateTypes.INVITE_COLLEAGUE_TO_EASYVISA;
  }


}
