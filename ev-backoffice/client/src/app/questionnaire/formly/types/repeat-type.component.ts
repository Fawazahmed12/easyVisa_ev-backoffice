import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { FieldArrayType } from '@ngx-formly/core';
import { FormlyFieldConfig } from '@ngx-formly/core/lib/components/formly.field.config';
import { BehaviorSubject, combineLatest, Observable, Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';

import { QuestionnaireService } from '../../services';
import { RepeatGroupAddRequestItem, RepeatGroupRemoveRequestItem } from '../../models/questionnaire.model';
import { ModalService } from '../../../core/services';
import { ConfirmButtonType } from '../../../core/modals/confirm-modal/confirm-modal.component';
import { DeleteRepeatGroupFailure, PostRepeatGroupFailure } from '../../ngrx/questionnaire/questionnaire.actions';


@Component({
  selector: 'app-formly-repeat',
  template: `
    <div *ngFor="let field of field.fieldGroup; let i = index;" class="repeat-container">
      <div class="repeat-index" *ngIf="params.showInstanceCount"> {{getRepeatingInstanceCount()}}</div>
      <formly-group
        [field]="field"
      >
        <span
          class="remove-btn" *ngIf="params.showRemoveButton"
          role="button" tabindex="-1"
          (click)="onRemoveRepeatingGroupInstanceClick(i)"
          ngbTooltip="Delete the iteration"
          [placement]="['bottom', 'auto']" tooltipClass="remove-iteration-tooltip questionnaire-tooltip"
          container="body"
          [ngClass]="{'disabled':(isLoading$ | async)}"
        >
          <i class="fa fa-times"></i>
        </span>

      </formly-group>
    </div>
    <div class="row" *ngIf="params.showAddButton">
      <div class="col-12 my-2 text-center">
        <button
          class="btn btn-primary  px-4 mr-4 min-w-100"
          type="button"
          (click)="addRepeatingGroupInstance()"
          [disabled]="(isLoading$ | async)"
        >{{params.addButtonTitle}}
        </button>
      </div>
    </div>

    <ng-template #removeRepeatingGroupModal>
      <div [innerHtml]="getRepeatingGroupWarningText()"></div>
    </ng-template>
  `,
  styles: [
      `
      .repeat-container {
        position: relative;
        margin-top: 30px;
      }

      .repeat-index {
        position: absolute;
        left: 2px;
        top: -28px;
        font-weight: bold;
        color: red;
        font-size: 1.5rem;
      }

      .remove-btn {
        position: absolute;
        right: 0px;
        top: -30px;
        color: red;
        font-size: 24px;
        cursor: pointer;
        background: none;
        border: none;
      }

      .remove-btn.disabled {
        cursor: default;
        pointer-events: none;
        opacity: 0.4;
      }

      .remove-btn:disabled {
        opacity: 0.5;
      }

      .remove-btn:focus {
        outline: none;
      }
    `
  ]
})
export class RepeatTypeComponent extends FieldArrayType implements OnInit, OnDestroy {

  field: FormlyFieldConfig & {
    params: {
      addButtonTitle: string;
      removeButtonTitle: string;
      showAddButton: boolean;
      showRemoveButton: boolean;
    };
  };
  params;

  isLoading$: Observable<boolean>;
  private repeatGroupAddResponseSubscription: Subscription;
  private repeatAddErrorAccessSubscription: Subscription;
  private repeatDeleteErrorAccessSubscription: Subscription;
  private pendingRequestSubject: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  @ViewChild('removeRepeatingGroupModal', { static: true }) removeRepeatingGroupModal;


  constructor(private questionnaireService: QuestionnaireService,
              private modalService: ModalService) {
    super();
  }

  ngOnInit() {
    this.isLoading$ = combineLatest([ this.pendingRequestSubject.asObservable(),
      this.questionnaireService.hasPendingAnswerSaveRequest$,
      this.questionnaireService.questionnaireAccessData$ ])
      .pipe(
        map(([ pendingRequestSubject, hasPendingAnswerSaveRequest, questionnaireAccessData ]) => {
          if (questionnaireAccessData && questionnaireAccessData.readOnly) {
            return true;
          }
          return pendingRequestSubject || hasPendingAnswerSaveRequest;
        })
      );

    this.params = this.field.params || {
      showRemoveButton: this.canShowRemoveButton(),
      showAddButton: this.canShowAddButton(),
      removeButtonTitle: 'Remove',
      addButtonTitle: this.getAddButtonTitle(),
      showInstanceCount: this.canShowRepeatingInstanceCount()
    };

    this.repeatGroupAddResponseSubscription = this.questionnaireService.postRepeatGroupSuccessAction$
      .pipe(filter((action) => {
        const attributes = this.getTemplateOptionAttributes();
        return attributes.repeatingGroupId === this.getResponseReferenceId(action);
      }))
      .subscribe((action) => {
        this.pendingRequestSubject.next(false);
      });

    this.repeatAddErrorAccessSubscription = this.questionnaireService.postRepeatGroupFailAction$
      .pipe(filter((action: PostRepeatGroupFailure) => this.questionnaireAccessErrorFilter(action)))
      .subscribe((data) => this.questionnaireAccessErrorHandler(data));

    this.repeatDeleteErrorAccessSubscription = this.questionnaireService.deleteRepeatGroupFailAction$
      .pipe(filter((action: DeleteRepeatGroupFailure) => this.questionnaireAccessErrorFilter(action)))
      .subscribe((data) => this.questionnaireAccessErrorHandler(data));
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
    const attributes = this.getTemplateOptionAttributes();
    if (attributes.repeatingGroupId !== sourceFieldId) {
      return false;
    }
    accessError.message = errorMessage;
    return true;
  }


  private questionnaireAccessErrorHandler(action) {
    const payload: HttpErrorResponse = action.payload as HttpErrorResponse;
    this.modalService.showErrorModal(payload.error.errors || [ payload.error ]);
  }


  getAddButtonTitle(): string {
    const attributes = this.getTemplateOptionAttributes();
    return (attributes.addButtonTitle as string) || 'Add Another';
  }

  canShowAddButton() {
    const attributes = this.getTemplateOptionAttributes();
    if (attributes.showAddButton !== undefined) {
      const showAddButton: string = attributes.showAddButton as string;
      return showAddButton;
    }
    const totalRepeatCount: number = attributes.totalRepeatCount as number;
    const answerIndex: number = attributes.answerIndex as number;
    return ((answerIndex + 1) === totalRepeatCount);
  }

  canShowRemoveButton() {
    const attributes = this.getTemplateOptionAttributes();
    if (attributes.showRemoveButton !== undefined) {
      const showRemoveButton: string = attributes.showRemoveButton as string;
      return showRemoveButton;
    }
    return true;
  }

  canShowRepeatingInstanceCount() {
    const attributes = this.getTemplateOptionAttributes();
    const totalRepeatCount: number = attributes.totalRepeatCount as number;
    return (totalRepeatCount !== 1);
  }

  getRepeatingInstanceCount() {
    const attributes = this.getTemplateOptionAttributes();
    const answerIndex: number = attributes.answerIndex as number;
    return answerIndex + 1; // as 'answerIndex' is zero based. So add '1'
  }


  addRepeatingGroupInstance() {
    const attributes = this.getTemplateOptionAttributes();
    const addRepeatGroupRequestItem: RepeatGroupAddRequestItem = {
      subsectionId: attributes.subsectionId as string,
      repeatingGroupId: attributes.repeatingGroupId as string
    };
    this.pendingRequestSubject.next(true);
    this.questionnaireService.repeatGroupAddRequest(addRepeatGroupRequestItem);
  }

  onRemoveRepeatingGroupInstanceClick() {
    if (!this.hasContainsAnyCompletedAnswer()) {
      return this.removeRepeatingGroupInstance();
    }
    this.openRemoveRepeatingGroupModal()
      .subscribe(() => this.removeRepeatingGroupInstance());
  }

  removeRepeatingGroupInstance() {
    const attributes = this.getTemplateOptionAttributes();
    const removeRepeatGroupRequestItem: RepeatGroupRemoveRequestItem = {
      subsectionId: attributes.subsectionId as string,
      repeatingGroupId: attributes.repeatingGroupId as string,
      index: attributes.answerIndex as number
    };
    this.pendingRequestSubject.next(true);
    this.questionnaireService.repeatGroupRemoveRequest(removeRepeatGroupRequestItem);
  }

  openRemoveRepeatingGroupModal() {
    const buttons = [
      {
        label: 'FORM.BUTTON.CANCEL',
        type: ConfirmButtonType.Dismiss,
        className: 'btn btn-primary mr-2 min-w-100',
      },
      {
        label: 'FORM.BUTTON.DELETE',
        type: ConfirmButtonType.Close,
        className: 'btn btn-primary mr-2 min-w-100',
      },
    ];

    return this.modalService.openConfirmModal({
      header: 'Remove Confirmation',
      body: this.removeRepeatingGroupModal,
      buttons,
      centered: true,
    });
  }

  hasContainsAnyCompletedAnswer() {
    const fieldArray = this.field.fieldArray;
    const repeatGroupQuestions = fieldArray.fieldGroup || [];
    const completedQuestions = repeatGroupQuestions.filter((questionData) => {
      const templateOptions = questionData.templateOptions || { attributes: { hasQuestionAnswered: true } };
      const attributes = templateOptions.attributes;
      return attributes[ 'hasQuestionAnswered' ];
    });
    return completedQuestions.length;
  }

  getRepeatingGroupWarningText() {
    const attributes = this.getTemplateOptionAttributes();
    const repeatingGroupDeleteText: String = attributes.repeatingGroupDeleteText as String;
    return repeatingGroupDeleteText;
  }


  getTemplateOptionAttributes() {
    const fieldArray = this.field.fieldArray;
    const templateOptions = fieldArray.templateOptions;
    const attributes = templateOptions.attributes;
    return attributes;
  }


  ngOnDestroy() {
    this.cleanupSubscriptions();
  }

  private getResponseReferenceId(action): string {
    const payloadData = action.payload;
    return payloadData.sourceFieldId;
  }


  private cleanupSubscriptions() {
    if (this.repeatGroupAddResponseSubscription) {
      this.repeatGroupAddResponseSubscription.unsubscribe();
      this.repeatGroupAddResponseSubscription = null;
    }
    if (this.repeatAddErrorAccessSubscription) {
      this.repeatAddErrorAccessSubscription.unsubscribe();
      this.repeatAddErrorAccessSubscription = null;
    }
    if (this.repeatDeleteErrorAccessSubscription) {
      this.repeatDeleteErrorAccessSubscription.unsubscribe();
      this.repeatDeleteErrorAccessSubscription = null;
    }
  }
}
