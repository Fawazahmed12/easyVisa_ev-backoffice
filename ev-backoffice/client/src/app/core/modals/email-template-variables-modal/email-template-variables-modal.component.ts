import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { EmailTemplatesService } from '../../services';
import { filter, map } from 'rxjs/operators';
import { EmailTemplateVariable } from '../../models/email-template-variable.enum';
import { Observable } from 'rxjs';
import { RequestState } from '../../ngrx/utils';
import { NotificationSettings } from '../../../account/models/notification-settings.model';

@Component({
  selector: 'app-email-template-variables-modal',
  templateUrl: 'email-template-variables-modal.component.html',
  styles: [`
    .template-variable-description{
      font-size:14px;
    }
  `]
})

export class EmailTemplateVariablesModalComponent implements OnInit {

  @Input() emailTemplateType;
  emailTemplateVariables$: Observable<any>;
  getEmailTemplateVariablesRequest$: Observable<RequestState<NotificationSettings>>;

  constructor(private activeModal: NgbActiveModal,
              private emailTemplatesService: EmailTemplatesService) {
  }

  ngOnInit() {
    this.getEmailTemplateVariablesRequest$ = this.emailTemplatesService.getEmailTemplateVariablesRequest$;
    this.emailTemplatesService.getEmailTemplateVariables({ emailTemplate: [this.emailTemplateType] });
    this.emailTemplateVariables$ = this.emailTemplatesService.emailTemplateVariables$.pipe(
      filter((res) => !!res),
      map((res)=>{
        const resVariables = res[ this.emailTemplateType ];
        const emailTemplateVariables = [];
        resVariables?.forEach((tempVariable) => {
          emailTemplateVariables.push({
            variable: EmailTemplateVariable[ tempVariable ],
            description: `TEMPLATE.EMAIL_TEMPLATE_VARIABLES_MODAL.${tempVariable}_DESCRIPTION`,
          });
        });
        return emailTemplateVariables;
      })
    );
  }

  modalDismiss() {
    this.activeModal.dismiss();
  }
}
