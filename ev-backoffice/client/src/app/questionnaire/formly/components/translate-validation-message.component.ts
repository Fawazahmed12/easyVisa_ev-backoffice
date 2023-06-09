import { Component, Input } from '@angular/core';

import { FormlyConfig, FormlyFieldConfig } from '@ngx-formly/core';

@Component({
  selector: 'app-transalate-validation-message',
  template: `{{ errorMessage | translate }}`,
})
export class TranslateValidationMessageComponent {
  @Input() field: FormlyFieldConfig;

  constructor(private formlyConfig: FormlyConfig) {
  }

  get errorMessage(): string {
    const fieldForm = this.field.formControl;
    for (const error in fieldForm.errors) {
      if (fieldForm.errors.hasOwnProperty(error)) {
        let message: string | Function = this.formlyConfig.getValidatorMessage(error);

        if (this.field.validation && this.field.validation.messages && this.field.validation.messages[ error ]) {
          message = this.field.validation.messages[ error ];
        }

        if (this.field.validators && this.field.validators[ error ] && this.field.validators[ error ].message) {
          message = this.field.validators[ error ].message;
        }

        if (this.field.asyncValidators && this.field.asyncValidators[ error ] && this.field.asyncValidators[ error ].message) {
          message = this.field.asyncValidators[ error ].message;
        }

        if (typeof message === 'function') {
          return message(fieldForm.errors[ error ], this.field);
        }

        return message;
      }
    }
  }
}
