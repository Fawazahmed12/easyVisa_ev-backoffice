import { Component, forwardRef, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ControlValueAccessor, FormArray, FormControl, NG_VALUE_ACCESSOR } from '@angular/forms';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { find } from 'lodash-es';

import {EMPTY, from, Subject, Observable, merge, combineLatest} from 'rxjs';
import { catchError, filter, map, mapTo, share, switchMap, withLatestFrom } from 'rxjs/operators';

import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ConfigDataService, ModalService, PackagesService } from '../../../../../../core/services';
import { ApplicantType } from '../../../../../../core/models/applicantType.enum';
import { OrganizationService } from '../../../../../../core/services';
import { FeeSchedule } from '../../../../../../core/models/fee-schedule.model';
import { ProcessRequestState } from '../../../../../../core/models/process-request-state.enum';
import { Package } from '../../../../../../core/models/package/package.model';
import { BenefitCategoryModel, Benefits } from '../../../../../../core/models/benefits.model';
import { PackageStatus } from '../../../../../../core/models/package/package-status.enum';
import { BenefitCategories } from '../../../../../../core/models/benefit-categories.enum';

import { CreateApplicantFormGroupService } from '../../../../services';

import { PetitionerBenefitModalComponent } from './modals/petitioner-benefit-modal/petitioner-benefit-modal.component';
import { ImmigrationBenefitModalComponent } from './modals/immigration-benefit-modal/immigration-benefit-modal.component';

@Component({
  selector: 'app-benefit-category',
  templateUrl: './benefit-category.component.html',
  styleUrls: ['./benefit-category.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => BenefitCategoryComponent),
      multi: true
    }
  ]
})
@DestroySubscribers()
export class BenefitCategoryComponent implements AddSubscribers, OnInit, OnDestroy, ControlValueAccessor {
  @Input() citizenshipStatusForm: FormControl;
  @Input() feeFormControl: FormControl;
  @Input() applicantTypeFormValue: ApplicantType;
  @Input() optInFormControl: FormControl;
  @Input() registerFormControl: FormControl;
  @Input() inBlockedPackageFormControl: FormControl;
  @Input() inOpenPackageFormControl: FormControl;
  @Input() index = null;
  @ViewChild('petitionerNotSelectedModal', {static: true}) petitionerNotSelectedModal;
  @ViewChild('petitionerBenefitModal', {static: false}) petitionerBenefitModal;
  @ViewChild('immigrationBenefitModal', {static: true}) immigrationBenefitModal;
  feeSchedule$: Observable<FeeSchedule[]>;
  activePackage$: Observable<Package>;
  isSubmittedForm$: Observable<boolean>;
  allBenefitCategories$: Observable<BenefitCategoryModel[]>;
  selectedBenefitCategory;
  isShowSelectLegalStatusWarningSubject$: Subject<boolean> = new Subject<boolean>();
  benefits$: Observable<Benefits>;

  private petitionerBenefitModalSubject$: Subject<any> = new Subject<any>();
  private subscribers: any = {};

  ProcessRequestState = ProcessRequestState;
  PackageStatus = PackageStatus;

  private onChange: Function = (benefitCategory: string) => {
  };
  private onTouch: Function = () => {
  };

  get applicantsFormArray() {
    return this.createApplicantFormGroupService.formGroup.get('applicants') as FormArray;
  }

  get secondApplicantBenefitCategory(): BenefitCategories {
    const applicantsFormGroup = this.applicantsFormArray.controls;
    const [_, second, ...rest] = applicantsFormGroup;
    return second?.value.benefitCategory;
  }

  get isShowNotRegister$() {
    return this.activePackage$.pipe(
      filter(activePackage => !!activePackage),
      map(activePackage => activePackage.status === PackageStatus.OPEN && !this.registerFormControl.value),
    );
  }

  get isShowInOpenBlocked$() {
    return this.activePackage$.pipe(
      filter(activePackage => !!activePackage),
      mapTo(this.inBlockedPackageFormControl.value || this.inOpenPackageFormControl.value && this.registerFormControl.value)
    );
  }

  get isShowWarningNotGranted$() {
    return this.activePackage$.pipe(
      filter(activePackage => !!activePackage),
      mapTo(this.optInFormControl.value !== ProcessRequestState.ACCEPTED)
    );
  }

  get isWarningShow$() {
    return merge(
      this.isShowNotRegister$.pipe(filter(res => res)),
      this.isShowInOpenBlocked$.pipe(filter(res => res)),
      this.isShowWarningNotGranted$.pipe(filter(res => res)),
    );
  }

  constructor(
    private modalService: ModalService,
    private organizationService: OrganizationService,
    private createApplicantFormGroupService: CreateApplicantFormGroupService,
    private ngbModal: NgbModal,
    private packagesService: PackagesService,
    private configDataService: ConfigDataService,
  ) {
  }

  get representativeIdFormControl() {
    return this.createApplicantFormGroupService.formGroup.get('representativeId');
  }

  get citizenshipStatus() {
    const applicants = this.createApplicantFormGroupService.formGroup.get('applicants') as FormArray;
    return applicants.at(0).value.citizenshipStatus;
  }

  ngOnInit() {
    this.benefits$ = this.configDataService.benefits$;
    this.isSubmittedForm$ = this.createApplicantFormGroupService.submittedSubject$;
    this.feeSchedule$ = this.organizationService.currentRepresentativeFeeSchedule$;
    this.activePackage$ = this.packagesService.activePackage$;
    this.allBenefitCategories$ = this.configDataService.allBenefitCategories$.pipe(
      filter(allBenefitCategories => !!allBenefitCategories)
    );
  }

  addSubscribers() {
    const openBenefitCategoryModal$ = this.petitionerBenefitModalSubject$.pipe(
      switchMap((component) => this.openBenefitCategoryModal(component)),
      share()
    );

    this.subscribers.petitionerModalSubscription = openBenefitCategoryModal$
    .subscribe((benefitCategory) => {
      this.onChange(benefitCategory);
      this.selectedBenefitCategory = benefitCategory;
    });

    this.subscribers.petitionerPopUpSubscription = combineLatest([
      openBenefitCategoryModal$,
      this.organizationService.feeScheduleEntities$
    ]).pipe(map(([benefitCategory, feeScheduleEntities]) => {
      const feeSchedules = feeScheduleEntities[this.representativeIdFormControl.value];
      return [benefitCategory, feeSchedules];
    })).subscribe(([benefitCategory, feeSchedules]) => {
      const fee = find(feeSchedules, {benefitCategory});
      this.feeFormControl.patchValue(fee ? fee.amount : 0);
    });

    this.subscribers.legalStatusControlSubscription = this.citizenshipStatusForm.valueChanges.pipe(
      filter(value => !!value)
    ).subscribe(() => this.isShowSelectLegalStatusWarningSubject$.next(false));
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  openBenefitCategory() {
    if (this.index === 0 && this.citizenshipStatusForm.value === null) {
      this.isShowSelectLegalStatusWarningSubject$.next(true);
    } else if (this.applicantTypeFormValue === ApplicantType.PETITIONER || this.index === 0) {
      this.petitionerBenefitModalSubject$.next(PetitionerBenefitModalComponent);
    } else {
      this.petitionerBenefitModalSubject$.next(ImmigrationBenefitModalComponent);
    }
  }

  openBenefitCategoryModal(component) {
    const modalRef = this.ngbModal.open(component, {
      centered: true,
      windowClass: 'custom-modal-lg',
    });
    modalRef.componentInstance.benefitCategory = this.selectedBenefitCategory;
    modalRef.componentInstance.applicantType = this.applicantTypeFormValue;
    modalRef.componentInstance.citizenshipStatus = this.citizenshipStatusForm.value;
    return from(modalRef.result).pipe(
      catchError(() => EMPTY),
    );
  }

  writeValue(value: string): void {
    this.selectedBenefitCategory = value;
  }

  registerOnChange(fn: Function): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: Function): void {
    this.onTouch = fn;
  }
}
