import { Component, ViewChild, ViewContainerRef } from '@angular/core';
import { FieldWrapper } from '@ngx-formly/core';

@Component({
  selector: 'app-repeat-question-group',
  template: `
    <div class="repeat-question-group">
      <ng-container #fieldComponent></ng-container>
    </div>
  `,
  styles: [
      `
      .repeat-question-group {
        margin: 0 30px;
      }
    `
  ]
})
export class RepeatQuestionGroupWrapperComponent extends FieldWrapper {
  @ViewChild('fieldComponent', { read: ViewContainerRef, static: true }) fieldComponent: ViewContainerRef;
}
