import { Component, Input } from '@angular/core';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-auth-form-field',
  templateUrl: './auth-form-field.component.html',
  styles: [
    `
    ::-webkit-input-placeholder {
        color: #999999;
        font-style: italic;
      }

      ::-moz-placeholder {
        color: #999999;
        font-style: italic;
      }

      :-ms-input-placeholder {
        color: #999999;
        font-style: italic;
      }

      :-moz-placeholder {
        color: #999999;
        font-style: italic;
      }

    `
  ]
})
export class AuthFormFieldComponent {

  @Input() label: string;

  @Input() submitted = false;

  @Input() control: FormControl;

  @Input() type = 'text';

  @Input() required = false;
  @Input() showError = true;
  @Input() doubleNote = false;
  @Input() primaryTextStyle = false;
  @Input() smallMarginStyle = false;
  @Input() noRequired = false;
  @Input() passwordInput = false;
  @Input() contactInfoEmail = false;
  @Input() smallCell = false;
  @Input() showToolTips = false;
  @Input() col3Label = false;
  @Input() col4Label = false;
  @Input() placeHolder = '';
  @Input() extraMarginBottom = false;
}
