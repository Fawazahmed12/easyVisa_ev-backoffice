import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-auth-form-field-error',
  template: `
      <div
              [class.text-center]="textCenter"
              class="pr-2"
      >
          <ng-content></ng-content>
      </div>
  `
})
export class AuthFormFieldErrorComponent {
  @Input() textCenter = false;
}
