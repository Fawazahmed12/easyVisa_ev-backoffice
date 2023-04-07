import { Injectable } from '@angular/core';
import { FormlyFieldConfig, FormlyTemplateOptions } from '@ngx-formly/core';
import { Answer } from '../models/questionnaire.model';

@Injectable()
export class FocusManagerService {

  private activeFieldPath: string; // questionId+"_"+answerIndex
  private previousAnswerState: Answer;
  private previousFieldPath: string;

  constructor() {
  }

  onFocusElement(field, $event) {
    this.activeFieldPath = this.findActiveFieldPath(field);
  }

  addFieldFocusListener(formlyQuestionnaire: FormlyFieldConfig[]) {
    formlyQuestionnaire.forEach((fomlyField: FormlyFieldConfig) => {
      this.bindFieldFocus(fomlyField);
    });
    return formlyQuestionnaire;
  }

  private bindFieldFocus(field) {
    const formlyTemplateOptions: FormlyTemplateOptions = field.templateOptions;
    if (formlyTemplateOptions) {
      this.setFieldFocus(field);
      formlyTemplateOptions.focus = this.onFocusElement.bind(this);
    }

    let fieldGroup = field.fieldGroup || [];
    if (fieldGroup.length === 0 && field.fieldArray) {
      const fieldArray = field.fieldArray;
      fieldGroup = fieldArray.fieldGroup || [];
    }
    fieldGroup.forEach((f) => this.bindFieldFocus(f));
  }

  private setFieldFocus(field) {
    const currentFieldPath = this.findActiveFieldPath(field);
    field.focus = (currentFieldPath === this.activeFieldPath);
    field.opened = (currentFieldPath === this.activeFieldPath && (field.type === 'ev-ngdatepicker' || field.type === 'ev-ngselect'));
  }

  public findActiveFieldPath(field): string {
    const formlyTemplateOptions: FormlyTemplateOptions = field.templateOptions;
    const attributes = formlyTemplateOptions.attributes;
    const questionId = attributes.questionId as string;
    const answerIndex = attributes.answerIndex as number;
    return questionId + '-' + answerIndex;
  }

  public setActiveFieldPath(value) {
    this.activeFieldPath = value;
  }

  public setPreviousAnswerState(data: Answer) {
    this.previousAnswerState = data;
  }

  public getPreviousAnswerState(): Answer {
    return this.previousAnswerState;
  }

  public setPreviousFieldPath(data: string) {
    this.previousFieldPath = data;
  }

  public getPreviousFieldPath(): string {
    return this.previousFieldPath;
  }

  public isActiveField(field): boolean {
    return this.activeFieldPath === this.findActiveFieldPath(field);
  }

}
