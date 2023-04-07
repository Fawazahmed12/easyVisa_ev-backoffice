import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild, ViewContainerRef } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { FieldWrapper } from '@ngx-formly/core';
import { get } from 'lodash-es';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter } from 'rxjs/operators';

import { QuestionnaireService } from '../../services';
import { Answer, AnswerSaveRequestItem, AnswerValidationModel } from '../../models/questionnaire.model';
import { ModalService } from '../../../core/services';
import { PostAnswerFailure } from '../../ngrx/questionnaire/questionnaire.actions';
import { FocusManagerService } from '../../services/focusmanager.service';


@Component({
  selector: 'app-request-wrapper',
  template: `
    <div class="request-wrapper">
      <ng-template #fieldComponent></ng-template>
      <span class="spinner-container" *ngIf="isLoading$ | async">
        <img src="../../../../assets/images/spinner.gif"/>
      </span>
    </div>
  `,
  styles: [
      `
      .request-wrapper {
        position: relative;
      }

      .spinner-container {
        display: inline-block;
        position: absolute;
        top: -1px;
        right: 38px;
      }
    `
  ]
})
export class FieldRequestWrapperComponent extends FieldWrapper implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('fieldComponent', { read: ViewContainerRef, static: true }) fieldComponent: ViewContainerRef;

  isLoading$: Observable<boolean>;
  requestDelay = 50;

  private saveQuestionSubscription: Subscription;
  private saveResponseSubscription: Subscription;
  private answerValidationSubscription: Subscription;
  private questionAccessSubscription: Subscription;
  private pendingRequestSubject: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  constructor(private questionnaireService: QuestionnaireService,
              private modalService: ModalService,
              private focusManagerService: FocusManagerService,
              private element: ElementRef) {
    super();
  }

  ngOnInit() {
    this.cleanupSubscriptions();
    this.isLoading$ = this.pendingRequestSubject.asObservable();
    this.saveQuestionSubscription = this.formControl.valueChanges
      .pipe(
        debounceTime(this.requestDelay),
        filter(_ => this.hasValueChanged()),
        distinctUntilChanged(),
      ).subscribe((data) => {
        this.saveAnswer(data);
      });

    this.saveResponseSubscription = this.questionnaireService.hasPendingAnswerSaveRequest$
      .pipe(filter((hasPendingAnswerSaveRequest) => hasPendingAnswerSaveRequest==false))
      .subscribe((action) => {
        this.pendingRequestSubject.next(false);
        this.formControl.markAsUntouched();
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


    // In some situtation, before the server responds the user might have type extra characters in other inputs
    // the below code checks the exitsance of this screnario and save the changes
    // If user has not touched still there is difference between current and previous value,
    // then it means it is dirty, and needs to be saved
    const templateAttributes = this.getTemplateAttributes();
    const fieldKey = this.field.key as string;
    const currentModelValue = this.field.model[ fieldKey ] || '';
    const previousAnswerModel: Answer = this.focusManagerService.getPreviousAnswerState() || {};
    const previousValue = get(previousAnswerModel, templateAttributes.fieldPath, '');
    if (!this.formControl.untouched && currentModelValue !== previousValue) {
      this.saveAnswer(currentModelValue);
    }
  }

  ngAfterViewInit() {
    const inputEl = this.element.nativeElement.querySelector('input');
    if (this.focusManagerService.isActiveField(this.field) && inputEl) {
      inputEl.focus();
    }
  }

  resetAnswerValue(answerValidationData: AnswerValidationModel) {
    this.questionnaireService.resetAnswerValue(answerValidationData, this.field);
    this.formControl.markAsUntouched();
  }

  saveAnswer(value) {
    this.pendingRequestSubject.next(true);
    const attributes = this.getTemplateAttributes();
    const answerModel: AnswerSaveRequestItem = {
      questionId: attributes.questionId as string,
      subsectionId: attributes.subsectionId as string,
      value,
      index: attributes.answerIndex as number,
      hasAnswerCompleted: this.hasQuestionAnswered(),
    };
    const currentAnswerState: Answer = this.questionnaireService.formlyAnswerSubject.getValue();
    this.focusManagerService.setPreviousAnswerState(currentAnswerState);
    this.focusManagerService.setPreviousFieldPath(`${attributes.fieldPath}`);
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

  ngOnDestroy() {
    this.cleanupSubscriptions();
  }

  private getResponseReferenceId(action): string {
    const payloadData = action.payload;
    return payloadData.sourceFieldId;
  }


  // In ngx-formly we are updating model value only on 'change' event..

  private getTemplateAttributes() {
    const templateOptions = this.field.templateOptions;
    const attributes = templateOptions.attributes;
    return attributes;
  }

  // But for input type we are updating model on 'blur' event.. That's why here we are checking 'touched' property for input type..
  private hasValueChanged(): boolean {
    // whenever you enable/disable any control, the form model's property changes.
    // that's why checking with enabled fields
    if (this.field.type === 'input') {
      return this.formControl.dirty && !this.formControl.disabled && !this.formControl.untouched;
    }
    return this.formControl.dirty && !this.formControl.disabled;
  }

  private questionnaireAccessErrorFilter(action) {
    const ACCESS_ERROR_TYPE = 'INVALID_QUESTIONNAIRE_ACCESS';
    const payload: HttpErrorResponse = action.payload as HttpErrorResponse;
    const errors = payload.error.errors || [ payload.error ];
    const accessError = errors[ 0 ] || { type: '' };
    if (accessError.type !== ACCESS_ERROR_TYPE) {
      return false;
    }
    const errorMessages = accessError.message.split('|');
    const errorMessage = errorMessages[ 0 ];
    const sourceFieldId = errorMessages[ 1 ];
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
    this.modalService.showErrorModal(payload.error.errors || [ payload.error ]);
  }

  private cleanupSubscriptions() {
    if (this.saveResponseSubscription) {
      this.saveResponseSubscription.unsubscribe();
      this.saveResponseSubscription = null;
    }
    if (this.answerValidationSubscription) {
      this.answerValidationSubscription.unsubscribe();
      this.answerValidationSubscription = null;
    }
    if (this.saveQuestionSubscription) {
      this.saveQuestionSubscription.unsubscribe();
      this.saveQuestionSubscription = null;
    }
    if (this.questionAccessSubscription) {
      this.questionAccessSubscription.unsubscribe();
      this.questionAccessSubscription = null;
    }
  }
}
