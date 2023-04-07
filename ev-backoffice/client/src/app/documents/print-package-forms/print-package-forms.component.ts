import { FormControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { catchError, debounceTime, filter, switchMap, take, tap, withLatestFrom } from 'rxjs/operators';
import { EMPTY, Observable, of, Subject } from 'rxjs';
import { fromPromise } from 'rxjs/internal-compatibility';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { intersection, last } from 'lodash-es';

import * as FileSaver from 'file-saver';

import { ModalService, OrganizationService, PackagesService, UserService } from '../../core/services';
import { RequestState } from '../../core/ngrx/utils';
import { ConfirmButtonType } from '../../core/modals/confirm-modal/confirm-modal.component';
import { Organization } from '../../core/models/organization.model';
import { Role } from '../../core/models/role.enum';

import {
  FormContainsNativeAlphabet,
  FormsPackageApplicant,
  NativeAlphabetFormPages,
  PackageContinuationSheets,
  PackageForm
} from '../models/forms-sheets.model';
import { PrintFormsSheetsService } from '../services/print-forms-sheets.service';

import { DownloadPrintComponent } from './download-print/download-print.component';

@Component({
  selector: 'app-print-package-forms',
  templateUrl: './print-package-forms.component.html',
  styleUrls: ['./print-package-forms.components.scss']
})
@DestroySubscribers()
export class PrintPackageFormsComponent implements OnInit, OnDestroy, AddSubscribers {
  @Input() readOnlyAccess;
  @Input() selectAllEvent = new Subject<any>();
  selectAllForms:Boolean = false;
  selectAllContinuationSheets:Boolean = false;
  formGroup: FormGroup;
  selectedApplicants$: Observable<FormsPackageApplicant[]>;
  selectedApplicantsIds$: Observable<number[]>;
  currentPackageForms$: Observable<PackageForm[]>;
  currentContinuationSheets$: Observable<PackageContinuationSheets[]>;
  downloadFormsGetRequestState$: Observable<RequestState<any>>;
  printFormGetRequestState$: Observable<RequestState<any>>;
  downloadSubject$: Subject<boolean> = new Subject<boolean>();
  printSubject$: Subject<boolean> = new Subject<boolean>();
  resetFormSubject$: Subject<boolean> = new Subject<boolean>();
  printDownloadBlankSubject$: Subject<boolean> = new Subject<boolean>();
  isFormCheckSubject$: Subject<boolean> = new Subject<boolean>();
  isSheetCheckSubject$: Subject<boolean> = new Subject<boolean>();
  activeOrganization$: Observable<Organization>;
  isUser$: Observable<boolean>;

  formsContainNativeAlphabet;
  questionConflictData;

  @ViewChild('nativeAlphabetPopup') nativeAlphabetPopup;
  @ViewChild('questionnaireIncomplete') questionnaireIncomplete;
  @ViewChild('questionResponseConflict') questionResponseConflict;

  private subscribers: any = {};

  get noFormSheetSelected() {
    return this.formIdsFormControl.value.length === 0 && this.continuationSheetIdsFormControl.value.length === 0;
  }

  get formIdsFormControl() {
    return this.formGroup.get('formIds');
  }

  get continuationSheetIdsFormControl() {
    return this.formGroup.get('continuationSheetIds');
  }

  constructor(
    private printFormsSheetsService: PrintFormsSheetsService,
    private packagesService: PackagesService,
    private modalService: ModalService,
    private ngbModal: NgbModal,
    private organizationService: OrganizationService,
    private userService: UserService,
  ) {
    this.createFormGroup();
  }

  ngOnInit() {
    this.activeOrganization$ = this.organizationService.activeOrganization$;
    this.selectedApplicants$ = this.printFormsSheetsService.selectedApplicants$;
    this.selectedApplicantsIds$ = this.printFormsSheetsService.selectedApplicantsIds$;
    this.currentPackageForms$ = this.printFormsSheetsService.currentPackageForms$;
    this.currentContinuationSheets$ = this.printFormsSheetsService.currentContinuationSheets$;
    this.downloadFormsGetRequestState$ = this.printFormsSheetsService.downloadFormsGetRequestState$;
    this.printFormGetRequestState$ = this.printFormsSheetsService.printFormGetRequestState$;
    this.formsContainNativeAlphabet = Object.values(FormContainsNativeAlphabet);
    this.isUser$ = this.userService.hasAccess([Role.ROLE_USER]);
  }

  addSubscribers() {
    this.subscribers.downloadFormGroupSubjectSubscription = this.downloadSubject$.pipe(
      withLatestFrom(this.packagesService.activePackageId$),
      switchMap(([, id]) => {
        const isFormsContainNativeAlphabet = this.formIdsFormControl.value.some(
          item => this.formsContainNativeAlphabet.includes(item)
        );
        if (isFormsContainNativeAlphabet) {
          this.showFormInfoModalWhileDownloading(id);
          return EMPTY;
        }
        return this.printFormsSheetsService.downloadForms({
          packageId: id,
          // formInfoList is a combination of formId and applicantId, which is seperated by #
          formInfoList: this.formIdsFormControl.value,
          // continuationSheetInfoList is a combination of continuationSheetId and applicantId, which is seperated by #
          continuationSheetInfoList: this.continuationSheetIdsFormControl.value
        }).pipe(catchError(() => EMPTY)
        );
      })
    )
      .subscribe((data: any) => {
          FileSaver.saveAs(data.file, data.fileName);
        }
      );

    this.subscribers.printFormGroupSubjectSubscription = this.printSubject$.pipe(
      filter(() => this.formGroup.valid),
      withLatestFrom(this.packagesService.activePackageId$),
      switchMap(([, id]) => {
        const isFormsContainNativeAlphabet = this.formIdsFormControl.value.some(
          item => this.formsContainNativeAlphabet.includes(item)
        );
        if (isFormsContainNativeAlphabet) {
          this.showFormInfoModalWhilePrinting(id);
          return EMPTY;
        }
        return this.printFormsSheetsService.printUSCISForm({
          packageId: id,
          // formInfoList is a combination of formId and applicantId, which is seperated by #
          formInfoList: this.formIdsFormControl.value,
          // continuationSheetInfoList is a combination of continuationSheetId and applicantId, which is seperated by #
          continuationSheetInfoList: this.continuationSheetIdsFormControl.value
        }).pipe(catchError(() => EMPTY)
        );
      })
    )
      .subscribe((data: any) => {
          this.printForms(data);
        }
      );

    this.subscribers.resetFormSubscription = this.resetFormSubject$
      .subscribe(() => this.resetFormGroup());

    this.subscribers.applicantIdsFormControlSubscription = this.printDownloadBlankSubject$.pipe(
      withLatestFrom(this.packagesService.activePackageId$),
      switchMap(([, id]) => this.printFormsSheetsService.getBlankForms({ packageId: id }).pipe(
        catchError((error: HttpErrorResponse) => {
          if (error.status !== 401) {
            this.modalService.showErrorModal(error.error.errors || [error.error]);
          }
          return EMPTY;
        })
      )),
      switchMap(() => this.openPrintDownloadModal())
    ).subscribe();

    this.subscribers.continuationSheetIdsFormControlSubscription = this.continuationSheetIdsFormControl.valueChanges.pipe(
      filter(data => !!data && !!data.length),
      withLatestFrom(this.currentContinuationSheets$.pipe(
        filter(res => !!res),
      )),
      withLatestFrom(
        this.isUser$,
        this.isSheetCheckSubject$,
      ),
      debounceTime(100),
      switchMap(([[sheetIds, currentSheets], isUser, isChecked]) => {
        const lastSheetId = last(sheetIds);
        const currentSheet = currentSheets.find(
          item => item.continuationSheetId === lastSheetId);
        if (currentSheet.hasCompleted && currentSheet.questionConflictData && isChecked) {
          this.questionConflictData = currentSheet.questionConflictData;
          this.openQuestionResponseConflictModal();
          return EMPTY;
        } else if (!currentSheet.hasCompleted && isChecked) {
          // if the form is incomplete and user is an applicant, then don't allow them to select
          if (isUser) {
            const updatedValue = sheetIds.filter(sheetId => sheetId !== lastSheetId);
            this.continuationSheetIdsFormControl.patchValue(updatedValue, { emitEvent: false });
          }
          return of(true);
        } else {
          return EMPTY;
        }
      }),
    ).subscribe(() => this.openQuestionnaireIncompleteModal());

    this.subscribers.formIdsFormControlSubscription = this.formIdsFormControl.valueChanges.pipe(
      filter(data => !!data && !!data.length),
      withLatestFrom(this.currentPackageForms$.pipe(
        filter(res => !!res),
      )),
      withLatestFrom(
        this.isUser$,
        this.isFormCheckSubject$,
      ),
      debounceTime(100),
      switchMap(([[formIds, currentPackageForms], isUser, isChecked]) => {
        const lastFormId = last(formIds);
        const currentPackageForm = currentPackageForms.find(
          item => item.formId === lastFormId);
        if (currentPackageForm.hasCompleted && currentPackageForm.questionConflictData && isChecked) {
          this.questionConflictData = currentPackageForm.questionConflictData;
          this.openQuestionResponseConflictModal();
          return EMPTY;
        } else if (!currentPackageForm.hasCompleted && isChecked) {
          // if the form is incomplete and user is an applicant, then don't allow them to select
          if (isUser) {
            const updatedValue = formIds.filter(formId => formId !== lastFormId);
            this.formIdsFormControl.patchValue(updatedValue, { emitEvent: false });
          }
          return of(true);
        } else {
          return EMPTY;
        }
      }),
    ).subscribe(() => this.openQuestionnaireIncompleteModal());
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  createFormGroup() {
    this.formGroup = new FormGroup({
      formIds: new FormControl([]),
      continuationSheetIds: new FormControl([]),
    }, {
      validators: [
        this.isValidForPrint(
          'formIds',
          'continuationSheetIds')
      ]
    });
  }

  resetFormGroup(
    data = {
      formIds: [],
      continuationSheetIds: []
    }
  ) {
    this.formGroup.reset({
      formIds: data.formIds,
      continuationSheetIds: data.continuationSheetIds,
    });
  }

  openPrintDownloadModal() {
    const modalRef = this.ngbModal.open(DownloadPrintComponent, {
      centered: true,
      size: 'lg',
    });
    return fromPromise(modalRef.result).pipe(
      catchError(() => EMPTY),
    );
  }

  downloadFiles() {
    this.downloadSubject$.next(true);
  }

  printFiles() {
    this.printSubject$.next(true);
  }

  resetForm() {
    this.resetFormSubject$.next(true);
  }

  printDownloadBlank() {
    this.printDownloadBlankSubject$.next(true);
  }

  openFormsContainNativeAlphabetModal() {
    const buttons = [
      {
        label: 'FORM.BUTTON.OK',
        type: ConfirmButtonType.Close,
        className: 'btn btn-primary mr-2 min-w-100',
      }
    ];

    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.PRINT_FORMS.NATIVE_ALPHABET_POP_UP_TITLE',
      body: this.nativeAlphabetPopup,
      buttons,
      centered: true,
      showCloseIcon: false,
      size: 'lg'
    }).pipe(
      catchError(() => EMPTY)
    );
  }

  openQuestionnaireIncompleteModal() {
    const buttons = [
      {
        label: 'FORM.BUTTON.OK',
        type: ConfirmButtonType.Close,
        className: 'btn btn-primary mr-2 min-w-100',
      }
    ];

    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.MODALS.QUESTIONNAIRE_INCOMPLETE.HEADER',
      body: this.questionnaireIncomplete,
      buttons,
      centered: true,
      showCloseIcon: false,
      size: 'lg'
    }).pipe(
      catchError(() => EMPTY)
    );
  }

  showFormInfoModalWhilePrinting(id) {
    this.openFormsContainNativeAlphabetModal()
      .subscribe((data) => this.printFormsSheetsService.printUSCISForm({
          packageId: id,
          // formInfoList is a combination of formId and applicantId, which is seperated by #
          formInfoList: this.formIdsFormControl.value,
          // continuationSheetInfoList is a combination of continuationSheetId and applicantId, which is seperated by #
          continuationSheetInfoList: this.continuationSheetIdsFormControl.value
        }).pipe(
          take(1),
          catchError(() => EMPTY)
        ).subscribe((printFormData: any) => {
            this.printForms(printFormData);
          }
        ));
  }

  showFormInfoModalWhileDownloading(id) {
    this.openFormsContainNativeAlphabetModal()
      .subscribe((data) => this.printFormsSheetsService.downloadForms({
          packageId: id,
          // formInfoList is a combination of formId and applicantId, which is seperated by #
          formInfoList: this.formIdsFormControl.value,
          // continuationSheetInfoList is a combination of continuationSheetId and applicantId, which is seperated by #
          continuationSheetInfoList: this.continuationSheetIdsFormControl.value
        }).pipe(
          take(1),
          catchError(() => EMPTY)
        ).subscribe((downloadFormData: any) => {
            FileSaver.saveAs(downloadFormData.file, downloadFormData.fileName);
          }
        ));
  }

  printForms(data) {
    const file = data.file;
    const fileType = file.type;
    if (fileType === 'application/pdf') {
      const fileBlob = new Blob([file], { type: fileType });
      const fileURL = URL.createObjectURL(fileBlob);
      window.open(fileURL, '_blank');
    } else {
      FileSaver.saveAs(file, data.fileName);
    }
  }

  getNativeAlphabetFormNames() {
    const forms = intersection(this.formIdsFormControl.value, this.formsContainNativeAlphabet);
    return forms.join(',');
  }

  onSelectAll(isChecked,type){
    this.selectAllEvent.next({ isChecked,type })
  }

  onCheckBoxChange({checked,type}){
    if(type == 'continuationSheets'){
      this.selectAllContinuationSheets = checked;
    }else {
      this.selectAllForms = checked;
    }
  }

  getNativeAlphabetFormPages() {
    const formPages = [];
    const forms = intersection(this.formIdsFormControl.value, this.formsContainNativeAlphabet);
    forms.forEach((form) => {
      formPages.push(NativeAlphabetFormPages[ form ]);
    });
    return formPages.join(',');
  }

  isFormCheck(check) {
    this.isFormCheckSubject$.next(check);
  }

  isSheetCheck(check) {
    this.isSheetCheckSubject$.next(check);
  }

  private isValidForPrint(formIds: string, continuationSheetIds: string): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {
      const formIdsFormControlLength = formGroup.get(`${formIds}`).value.length;
      const continuationSheetIdsFormControlLength = formGroup.get(`${continuationSheetIds}`).value.length;
      const invalidFormIdsFormControlLength = formIdsFormControlLength > 1;
      const invalidContinuationSheetIdsFormControlLength = continuationSheetIdsFormControlLength > 1;
      const invalidQuantitySelected = invalidFormIdsFormControlLength || invalidContinuationSheetIdsFormControlLength;
      const bothSelected = formIdsFormControlLength && continuationSheetIdsFormControlLength;

      return invalidQuantitySelected || bothSelected ? { invalid: true } : null;
    };
  }

  openQuestionResponseConflictModal() {
    const buttons = [
      {
        label: 'FORM.BUTTON.OK',
        type: ConfirmButtonType.Close,
        className: 'btn btn-primary mr-2 min-w-100',
      }
    ];

    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.MODALS.QUESTION_RESPONSE_CONFLICT.HEADER',
      body: this.questionResponseConflict,
      buttons,
      centered: true,
      showCloseIcon: false,
      size: 'lg'
    }).pipe(
      tap(() => this.questionConflictData = null),
      catchError(() => EMPTY)
    );
  }
}
