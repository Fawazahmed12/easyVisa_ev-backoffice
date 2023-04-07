import {Component, OnDestroy, OnInit} from '@angular/core';
import {HttpErrorResponse} from '@angular/common/http';

import {FieldType} from '@ngx-formly/core';
import {BehaviorSubject, Observable, Subscription} from 'rxjs';
import {filter} from 'rxjs/operators';

import {isEmpty} from 'lodash-es';

import {QuestionnaireService} from '../../services';
import {AnswerSaveRequestItem, AnswerValidationModel} from '../../models/questionnaire.model';
import {ModalService} from '../../../core/services';
import {PostAnswerFailure} from '../../ngrx/questionnaire/questionnaire.actions';
import {FocusManagerService} from '../../services/focusmanager.service';

@Component({
  template: ''
})
export class BaseFieldTypeComponent extends FieldType implements OnInit, OnDestroy {

  isLoading$: Observable<boolean>;
  modalService: ModalService;
  focusManagerService: FocusManagerService;
  questionnaireService: QuestionnaireService;

  private saveResponseSubscription: Subscription;
  private questionAccessSubscription: Subscription;
  private answerValidationSubscription: Subscription;
  private pendingRequestSubject: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  constructor(questionnaireService: QuestionnaireService,
              focusManagerService: FocusManagerService,
              modalService: ModalService) {
    super();
    this.modalService = modalService;
    this.questionnaireService = questionnaireService;
    this.focusManagerService = focusManagerService;
  }

  ngOnInit() {
    this.cleanupSubscriptions();
    this.isLoading$ = this.pendingRequestSubject.asObservable();

    this.saveResponseSubscription = this.questionnaireService.hasPendingAnswerSaveRequest$
      .pipe(filter((hasPendingAnswerSaveRequest) => hasPendingAnswerSaveRequest == false))
      .subscribe((action) => {
        this.pendingRequestSubject.next(false);
      });

    this.answerValidationSubscription = this.questionnaireService.answerValidationData$
      .pipe(filter((answerValidationData) => !!answerValidationData),
      ).subscribe((answerValidationData) => {
        const attributes = this.getTemplateAttributes();
        const previousFieldPath: string = this.focusManagerService.getPreviousFieldPath() || '';
        if (!answerValidationData.hasValidAnswer && attributes.fieldPath === previousFieldPath) {
          this.pendingRequestSubject.next(false);
          this.resetAnswerValue(answerValidationData);
        }
      });

    this.questionAccessSubscription = this.questionnaireService.postAnswerFailAction$
      .pipe(filter((action: PostAnswerFailure) => this.questionnaireAccessErrorFilter(action)))
      .subscribe((data) => this.questionnaireAccessErrorHandler(data));

    this.initializeModelValue();
  }

  initializeModelValue() {

  }

  saveAnswer(answerValue: string) {
    this.pendingRequestSubject.next(true);
    this.answerSaveRequest(answerValue);
  }

  isDisabled() {
    const attributes = this.getTemplateAttributes();
    const templateOptions = this.field.templateOptions;
    templateOptions.disabled = !!attributes.disabled;
    return attributes.disabled;
  }

  getTemplateAttributes() {
    const templateOptions = this.field.templateOptions;
    const attributes = templateOptions.attributes;
    return attributes;
  }

  ngOnDestroy() {
    this.cleanupSubscriptions();
  }

  private answerSaveRequest(answerValue: string) {
    const templateOptions = this.field.templateOptions;
    const attributes = templateOptions.attributes;
    this.focusManagerService.setPreviousFieldPath(`${attributes.fieldPath}`);
    const answerModel: AnswerSaveRequestItem = {
      questionId: attributes.questionId as string,
      subsectionId: attributes.subsectionId as string,
      value: answerValue,
      index: attributes.answerIndex as number,
      hasAnswerCompleted: this.hasQuestionAnswered(),
    };
    const hasValidationRequired = !!attributes.hasValidationRequired;
    if (hasValidationRequired) {
      this.questionnaireService.answerValidationRequest(answerModel);
    } else {
      this.questionnaireService.answerSaveRequest(answerModel);
    }
  }

  hasQuestionAnswered(): boolean {
    const attributes = this.getTemplateAttributes();
    if (typeof attributes.hasQuestionAnswered === 'string') {
      return attributes.hasQuestionAnswered === 'true';
    }
    return !!attributes.hasQuestionAnswered;
  }

  private cleanupSubscriptions() {
    if (this.saveResponseSubscription) {
      this.saveResponseSubscription.unsubscribe();
      this.saveResponseSubscription = null;
    }
    if (this.questionAccessSubscription) {
      this.questionAccessSubscription.unsubscribe();
      this.questionAccessSubscription = null;
    }
    if (this.answerValidationSubscription) {
      this.answerValidationSubscription.unsubscribe();
      this.answerValidationSubscription = null;
    }
  }

  private getResponseReferenceId(action): string {
    const payloadData = action.payload;
    return payloadData.sourceFieldId;
  }

  private questionnaireAccessErrorFilter(action) {
    const ACCESS_ERROR_TYPE = 'INVALID_QUESTIONNAIRE_ACCESS';
    const payload: HttpErrorResponse = action.payload as HttpErrorResponse;
    const errors = payload.error.errors || [payload.error];
    const accessError = errors[0] || {type: ''};
    if (accessError.type !== ACCESS_ERROR_TYPE) {
      return false;
    }
    const errorMessages = accessError.message.split('|');
    const errorMessage = errorMessages[0];
    const sourceFieldId = errorMessages[1];
    const attributes = this.getTemplateAttributes();
    if (attributes.questionId !== sourceFieldId) {
      return false;
    }
    accessError.message = errorMessage;
    return true;
  }

  private questionnaireAccessErrorHandler(action) {
    this.pendingRequestSubject.next(false);
    const payload: HttpErrorResponse = action.payload as HttpErrorResponse;
    this.modalService.showErrorModal(payload.error.errors || [payload.error]);
  }

  resetAnswerValue(answerValidationData: AnswerValidationModel) {
    this.questionnaireService.resetAnswerValue(answerValidationData, this.field);

    const resetValue = answerValidationData.resetValue || '';
    this.resetModelValue(resetValue);
    this.formControl.markAsUntouched();
  }

  // Every type should override this method and update its corresponding ngModel values.
  // Example: See ev-ngdatepicker.component.ts and ev-ngselect.component.ts
  resetModelValue(resetValue) {
    // implement this in each custom type formly component
  }
}
