import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { catchError, filter, mapTo, startWith, switchMap, withLatestFrom } from 'rxjs/operators';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { combineLatest, EMPTY, merge, Observable, ReplaySubject, Subject } from 'rxjs';

import { EmailTemplateVariablesModalComponent } from '../../core/modals/email-template-variables-modal/email-template-variables-modal.component';
import {
  ConfigDataService,
  EmailTemplatesService,
  ModalService,
  NotificationsService,
  OrganizationService,
  PackagesService
} from '../../core/services';
import { ConfirmButtonType, OkButton, OkButtonLg } from '../../core/modals/confirm-modal/confirm-modal.component';
import { EmailTemplateTypes } from '../../core/models/email-template-types.enum';
import { GovernmentFee } from '../../core/models/government-fee.model';
import { EmailTemplate } from '../../core/models/email-template.model';
import { Package } from '../../core/models/package/package.model';
import { ActivePackageComponent } from '../../components/active-package/active-package.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';


@Component({
  selector: 'app-additional-fees',
  templateUrl: './additional-fees.component.html',
})
@DestroySubscribers()
export class AdditionalFeesComponent implements OnInit, OnDestroy, AddSubscribers {
  @ViewChild('governmentFeesModal', { static: true }) governmentFeesModal;
  @ViewChild('editAmountOwedModal', { static: true }) editAmountOwedModal;

  activePackage$: Observable<Package>;
  governmentFee$: Observable<GovernmentFee>;
  openEditAmountOwedModal$ = new Subject();
  clearEntireBillSubject$: Subject<boolean> = new Subject();
  setActivePackageSubject$: Subject<boolean> = new Subject();
  sendFeesBillSubject$: Subject<boolean> = new Subject();
  resetSubject$: ReplaySubject<boolean> = new ReplaySubject<boolean>(1);
  total$: ReplaySubject<number> = new ReplaySubject<number>(1);

  formGroup: FormGroup;
  owedFormControl = new FormControl(null, Validators.min(0));

  private subscribers: any = {};

  constructor(
    private modalService: ModalService,
    private packagesService: PackagesService,
    private configDataService: ConfigDataService,
    private organizationService: OrganizationService,
    private notificationsService: NotificationsService,
    private emailTemplatesService: EmailTemplatesService,
    private ngbModal: NgbModal
  ) {
    this.createFormGroup();
  }

  get chargesFormArray() {
    return this.formGroup.get('charges') as FormArray;
  }

  get emailFormControl() {
    return this.formGroup.get('email');
  }

  ngOnInit() {
    this.notificationsService.showComponent$.next(ActivePackageComponent);
    this.activePackage$ = this.packagesService.activePackage$;
    this.governmentFee$ = this.configDataService.governmentFee$;
  }

  addSubscribers() {

    this.subscribers.packageSubscription = combineLatest([
      this.activePackage$,
      this.setActivePackageSubject$
    ]).pipe(
      filter(([ value, hasActivePackage ]) => !!value)
    ).subscribe(([ currentPackage, hasActivePackage ]) => {
        this.formGroup.get('packageId').patchValue(currentPackage.id);
        this.formGroup.get('representativeId').patchValue(currentPackage.representativeId);
      });
    this.setActivePackageSubject$.next(true);

    this.subscribers.representativeIdSubscription = combineLatest([
      this.organizationService.currentRepresentativeId$,
      this.resetSubject$.pipe(startWith(false))
    ]).pipe(
      filter(([ currentRepresentativeId, ]) => typeof currentRepresentativeId !== 'undefined'),
      switchMap(([ representativeId, ]) => this.emailTemplatesService.getEmailTemplate(
        {
          templateType: EmailTemplateTypes.ADDITIONAL_FEES,
          representativeId
        }).pipe(
        catchError((error: HttpErrorResponse) => {
            if (error.status !== 401) {
              this.modalService.showErrorModal(error.message || error.error.errors || [ error.error ]);
            }
            return EMPTY;
          }
        ),
      )),
      filter((res) => !!res),
    ).subscribe((emailTemplate: EmailTemplate) => {
      this.formGroup.patchValue({ email: emailTemplate.content, content: emailTemplate.content });
    });

    this.subscribers.editOwedModalSubscription = this.openEditAmountOwedModal$.pipe(
      withLatestFrom(this.activePackage$),
      switchMap(([ ngTemplate, packageItem ]) =>
        this.createEditAmountOwedModal(ngTemplate).pipe(
          catchError(() => {
            this.owedFormControl.reset(packageItem.owed);
            return EMPTY;
          }),
          mapTo(packageItem),
        )
      ),
      filter(() => this.owedFormControl.valid),
      filter((packageItem) => this.owedFormControl.value !== packageItem.owed),
      switchMap((packageItem) => this.packagesService.updatePackage({
        ...packageItem,
        owed: this.owedFormControl.value
      })),
      catchError(() => EMPTY)
    ).subscribe();

    this.subscribers.clearEntireBillSubjectSubscription = this.clearEntireBillSubject$
      .subscribe(() => {
        this.createFormGroup();
        this.resetSubject$.next(true);
      });

    this.subscribers.sendBillSubjectSubscription = this.sendFeesBillSubject$.pipe(
      filter(() => this.formGroup.valid),
      switchMap(() => this.packagesService.activePackageId$),
      switchMap((activePackageId) => this.packagesService.sendFeesBill(
          {
            packageId: activePackageId,
            ...this.formGroup.value
          }
        ).pipe(catchError(() => EMPTY))),
      catchError(() => EMPTY),
    )
      .subscribe(() => {
        this.createFormGroup();
        this.resetSubject$.next(true);
        this.setActivePackageSubject$.next(true);
      });
  }

  ngOnDestroy() {
    this.notificationsService.showComponent$.next(null);
  }

  openGovernmentFeesTableModal() {
    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.GOVERNMENT_FEES.TITLE',
      body: this.governmentFeesModal,
      buttons: [ OkButtonLg ],
      windowClass: 'custom-modal-lg',
      centered: true,
    });
  }

  countTotal() {
    let total = 0;
    this.chargesFormArray.value.map((charge) => {
      total += charge.each * charge.quantity;
    });
    this.total$.next(total);
  }

  addAnotherFeeRow() {
    this.chargesFormArray.push(this.createChargeFormGroup());
  }

  removeFeeRow(index) {
    if (this.chargesFormArray.length === 1) {
      this.chargesFormArray.setControl(index, this.createChargeFormGroup());
    } else {
      this.chargesFormArray.removeAt(index);
    }
  }

  createFormGroup() {
    this.formGroup = new FormGroup({
      email: new FormControl(null, Validators.required),
      content: new FormControl(null, Validators.required),
      charges: new FormArray([ this.createChargeFormGroup() ]),
      templateType: new FormControl(EmailTemplateTypes.ADDITIONAL_FEES),
      packageId: new FormControl(),
      representativeId: new FormControl()
    });
    this.subscribers.applicantsFormArraySubscription = merge(
      this.formGroup.valueChanges,
      this.resetSubject$,
    ).subscribe(() => this.countTotal());
  }

  createChargeFormGroup() {
    return new FormGroup({
      description: new FormControl(null, Validators.required),
      each: new FormControl(null, Validators.required),
      quantity: new FormControl(
        null,
        {
          updateOn: 'change',
          validators: [ Validators.required, Validators.pattern(/^(0|[1-9][0-9]*)$/) ],
        })
    });
  }

  openEmailTemplateVariablesModal() {
    const modalRef = this.ngbModal.open(EmailTemplateVariablesModalComponent, {
      centered: true
    });
    modalRef.componentInstance.emailTemplateType = EmailTemplateTypes.ADDITIONAL_FEES;
  }

  openEditAmountOwedModal(ngTemplate) {
    this.openEditAmountOwedModal$.next(ngTemplate);
  }

  clearEntireBill() {
    this.clearEntireBillSubject$.next(true);
    this.setActivePackageSubject$.next(true);
  }

  sendFeesBill() {
    this.sendFeesBillSubject$.next(true);
  }

  previewEmail(body, form) {
    if (this.formGroup.invalid) {
      form.submitted = true;
      return;
    }
    this.formGroup.get('content').patchValue(this.formGroup.get('email').value);
    this.modalService.openConfirmModal(
      {
        header: 'TEMPLATE.MODAL.PREVIEW_EMAIL_MODAL',
        buttons: [ OkButton ],
        windowClass: 'email-preview-modal',
        body,
      }
    );
  }

  createEditAmountOwedModal(ngTemplate) {
    const buttons = [
      {
        label: 'FORM.BUTTON.CANCEL',
        type: ConfirmButtonType.Dismiss,
        className: 'btn btn-primary mr-2 min-w-100',
      },
      {
        label: 'FORM.BUTTON.OK',
        type: ConfirmButtonType.Close,
        className: 'btn btn-primary mr-2 min-w-100',
      },
    ];

    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.TASK_QUEUE.CLIENTS.EDIT_AMOUNT_OWED.TITLE',
      body: ngTemplate,
      buttons,
    });
  }
}
