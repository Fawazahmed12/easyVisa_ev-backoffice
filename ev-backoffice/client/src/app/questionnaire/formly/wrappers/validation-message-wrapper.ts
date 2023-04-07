import { Component, ViewChild, ViewContainerRef } from '@angular/core';
import { FieldWrapper } from '@ngx-formly/core';

@Component({
  selector: 'app-wrapper-validation-messages',
  template: `
    <ng-template #fieldComponent></ng-template>
    <div *ngIf="showError">
      <small class="text-danger invalid-feedback" [style.display]="'block'" role="alert" [id]="validationId">
        <app-transalate-validation-message [field]="field"></app-transalate-validation-message>
      </small>
    </div>
  `,
})
export class ValidationMessageWrapperComponent extends FieldWrapper {
  @ViewChild('fieldComponent', { read: ViewContainerRef, static: true }) fieldComponent: ViewContainerRef;

  get validationId() {
    return this.field.id + '-message';
  }
}

