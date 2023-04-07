import { Component, ViewChild, ViewContainerRef } from '@angular/core';
import { FieldWrapper } from '@ngx-formly/core';

@Component({
  template: ''
})
export class BaseFieldWrapperComponent extends FieldWrapper {
  @ViewChild('fieldComponent', { read: ViewContainerRef, static: true }) fieldComponent: ViewContainerRef;

  canShowToolTip(): boolean {
    const templateOptions = this.field.templateOptions;
    return !!templateOptions.toolTip;
  }

  getToolTip() {
    const templateOptions = this.field.templateOptions;
    return templateOptions.toolTip;
  }

  hasQuestionAnswered(): boolean {
    const attributes = this.getTemplateAttributes();
    if (typeof attributes.hasQuestionAnswered === 'string') {
      return attributes.hasQuestionAnswered === 'true';
    }
    return !!attributes.hasQuestionAnswered;
  }

  getTemplateAttributes() {
    const templateOptions = this.field.templateOptions;
    const attributes = templateOptions.attributes;
    return attributes;
  }
}
