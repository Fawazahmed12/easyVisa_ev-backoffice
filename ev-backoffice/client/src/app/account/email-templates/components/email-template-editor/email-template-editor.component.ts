import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';

import { isEqual } from 'lodash-es';

import { combineLatest, Observable } from 'rxjs';
import { filter, map, startWith } from 'rxjs/operators';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { ModalService } from '../../../../core/services';
import { EmailTemplate } from '../../../../core/models/email-template.model';
import { RequestState } from '../../../../core/ngrx/utils';
import { EmailTemplatesService } from '../../../../core/services';
import {
  EmailTemplateVariablesModalComponent
} from '../../../../core/modals/email-template-variables-modal/email-template-variables-modal.component';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-email-template-editor',
  templateUrl: './email-template-editor.component.html',
})

@DestroySubscribers()
export class EmailTemplateEditorComponent implements OnInit, OnDestroy, AddSubscribers {
  @Input() representativeIdFormControl = new FormControl();
  @Input() emailTemplate: { type: string; title: string };
  @Input() blockEmail = false;
  @Input() index: number;

  emailTemplate$: Observable<EmailTemplate>;

  emailTemplateFormGroup: FormGroup;

  getEmailTemplateRequest$: Observable<RequestState<EmailTemplate>>;
  putEmailTemplateRequest$: Observable<RequestState<EmailTemplate>>;
  isSaveButtonDisabled$: Observable<any>;
  isChangeable = true;
  private subscribers: any = {};

  constructor(
    private modalService: ModalService,
    private emailTemplatesService: EmailTemplatesService,
    private ngbModal: NgbModal
  ) {
    this.createFormGroup();
  }

  ngOnInit() {
    this.emailTemplate$ = this.emailTemplatesService.emailTemplatesEntities$.pipe(
      map((entities) => entities[this.emailTemplate.type])
    );

    this.getEmailTemplateRequest$ = this.emailTemplatesService.getEmailTemplateRequest$;
    this.putEmailTemplateRequest$ = this.emailTemplatesService.putEmailTemplateRequest$;

    this.subscribers.emailTemplateSubscription = this.emailTemplate$.pipe(
      filter((data) => !!data && this.isChangeable),
    )
    .subscribe((template) => {
      this.emailTemplateFormGroup.patchValue({
        ...template,
        representativeId: this.representativeIdFormControl.value
      });
      this.isChangeable = false;
    });

    this.isSaveButtonDisabled$ = combineLatest([
      this.emailTemplateFormGroup.valueChanges.pipe(
        startWith<any, any>(this.emailTemplateFormGroup.value),
      ),
      this.emailTemplate$,
    ]).pipe(
      map(([formValue, template]) => {
        const comparedTemplate = {
          content: template ? template.content : null,
          subject: template ? template.subject : null,
        };
        const comparedFormValue = {
          content: formValue.content,
          subject: formValue.subject,
        };
        return isEqual(comparedTemplate, comparedFormValue) && template && !template.isDefault;
      })
    );
  }

  addSubscribers() {
    this.subscribers.representativeIdSubscription = this.representativeIdFormControl.valueChanges.pipe(
      filter((representativeId) => !!representativeId)
    )
    .subscribe((res) => {
      this.isChangeable = true;
      this.emailTemplateFormGroup.get('representativeId').patchValue(res);
    });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  createFormGroup(data?) {
    this.emailTemplateFormGroup = new FormGroup({
      subject: new FormControl(data ? data.subject : null),
      content: new FormControl(data ? data.content : null),
      representativeId: new FormControl(this.representativeIdFormControl.value),
      templateType: new FormControl(data ? data.templateType : null),
    });
  }

  openEmailTemplateVariablesModal() {
    const modalRef = this.ngbModal.open(EmailTemplateVariablesModalComponent, {
      centered: true
    });
    modalRef.componentInstance.emailTemplateType = this.emailTemplate.type;
  }

  saveEmailTemplate() {
    this.emailTemplatesService.updateEmailTemplate(this.emailTemplateFormGroup.value);
  }

  resetEmailTemplate() {
    this.isChangeable = true;
    this.emailTemplatesService.getEmailTemplate(this.emailTemplateFormGroup.value, true);
  }
}
