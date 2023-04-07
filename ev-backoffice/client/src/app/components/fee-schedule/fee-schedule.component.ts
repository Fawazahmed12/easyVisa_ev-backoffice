import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Dictionary } from '@ngrx/entity';

import { EMPTY, Observable, of, Subject } from 'rxjs';
import { catchError, filter, switchMap, withLatestFrom } from 'rxjs/operators';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { RequestState } from '../../core/ngrx/utils';
import { Attorney } from '../../core/models/attorney.model';

import { benefitCategories, BenefitCategoryConst } from '../../core/models/benefit-categories';
import { FeeSchedule } from '../../core/models/fee-schedule.model';
import { ConfigDataService, FeeScheduleService, ModalService, OrganizationService } from '../../core/services';
import { BenefitCategoryModel, BenefitGroupModel } from '../../core/models/benefits.model';
import { OkButton } from '../../core/modals/confirm-modal/confirm-modal.component';


@Component({
  selector: 'app-fee-schedule',
  templateUrl: './fee-schedule.component.html',
  styleUrls: ['./fee-schedule.component.scss']
})
@DestroySubscribers()
export class FeeScheduleComponent implements OnInit, OnDestroy, AddSubscribers {

  @Input() selectedRepresentativeFormControl: FormControl;
  @Input() isSettingsTab = false;

  updateAttorneyRequest$: Observable<RequestState<Attorney>>;
  feeScheduleFormGroup: FormGroup;
  benefitCategories: BenefitCategoryConst[] = benefitCategories;
  currentRepresentativeFeeSchedule$: Observable<FeeSchedule[]>;
  feeScheduleSettings$: Observable<FeeSchedule[]>;
  title$: Observable<string>;
  allBenefitGroups$: Observable<BenefitGroupModel[]>;
  allBenefitCategories$: Observable<BenefitCategoryModel[]>;
  noPetitionerBenefitCategories$: Observable<BenefitCategoryModel[]>;
  noDerivativesBenefitCategories$: Observable<BenefitCategoryModel[]>;
  withDerivativesBenefitCategories$: Observable<BenefitCategoryModel[]>;
  noPetitionerBenefitGroups$: Observable<BenefitGroupModel[]>;
  benefitGroupsNoDerivatives$: Observable<BenefitGroupModel[]>;
  benefitGroupsWithDerivatives$: Observable<BenefitGroupModel[]>;

  private feeSchedule$: Observable<FeeSchedule[]>;
  private representativeEntities$: Observable<Dictionary<Attorney>>;
  private updateFeeScheduleSubject$: Subject<boolean> = new Subject();
  private resetFeeScheduleSubject$: Subject<boolean> = new Subject();
  private subscribers: any = {};

  get feeFormGroups() {
    return this.feeScheduleFormGroup.get('noPetitionerFeeSchedule') as FormArray;
  }

  get noDerivativesFeeScheduleFeeFormGroups() {
    return this.feeScheduleFormGroup.get('noDerivativesFeeSchedule') as FormArray;
  }

  get withDerivativesFeeScheduleFeeFormGroups() {
    return this.feeScheduleFormGroup.get('withDerivativesFeeSchedule') as FormArray;
  }

  constructor(
    private organizationService: OrganizationService,
    private modalService: ModalService,
    private feeScheduleService: FeeScheduleService,
    private configDataService: ConfigDataService,
  ) {
  }

  ngOnInit() {
    this.currentRepresentativeFeeSchedule$ = this.organizationService.currentRepresentativeFeeSchedule$.pipe(
      filter((feeSchedule) => !!feeSchedule),
    );
    this.feeScheduleSettings$ = this.feeScheduleService.feeScheduleSettings$.pipe(
      filter((feeSchedule) => !!feeSchedule),
    );
    this.updateAttorneyRequest$ = this.organizationService.updateAttorneyRequest$;
    this.representativeEntities$ = this.organizationService.representativeEntities$;
    this.feeSchedule$ = this.isSettingsTab ? this.feeScheduleSettings$ : this.currentRepresentativeFeeSchedule$;
    this.title$ = of(this.isSettingsTab ? 'TEMPLATE.DASHBOARD.SETTINGS.CUSTOMER_FEES.FEE_SCHEDULE_CHARGED_TO_REP'
      : 'TEMPLATE.ACCOUNT.PAYMENT.TITLE_3');
    this.allBenefitGroups$ = this.configDataService.allBenefitGroups$;
    this.allBenefitCategories$ = this.configDataService.allBenefitCategories$.pipe(
      filter(allBenefitCategories => !!allBenefitCategories)
    );
    this.noPetitionerBenefitCategories$ = this.configDataService.noPetitionerBenefitCategories$.pipe(
      filter((data) => !!data),
    );
    this.noDerivativesBenefitCategories$ = this.configDataService.noDerivativesBenefitCategories$.pipe(
      filter((data) => !!data),
    );
    this.withDerivativesBenefitCategories$ = this.configDataService.withDerivativesBenefitCategories$.pipe(
      filter((data) => !!data),
    );
    this.noPetitionerBenefitGroups$ = this.configDataService.noPetitionerBenefitGroups$;
    this.benefitGroupsNoDerivatives$ = this.configDataService.benefitGroupsNoDerivatives$;
    this.benefitGroupsWithDerivatives$ = this.configDataService.benefitGroupsWithDerivatives$;
  }

  addSubscribers() {
    this.subscribers.representativesSubscription = this.feeSchedule$.pipe(
      filter(feeSchedule => !!feeSchedule),
      withLatestFrom(
        this.noPetitionerBenefitCategories$,
        this.noDerivativesBenefitCategories$,
        this.withDerivativesBenefitCategories$,
      )
    ).subscribe(
      ([
         feeSchedule,
         noPetitionerBenefitCategories,
         noDerivativesBenefitCategories,
         withDerivativesBenefitCategories,
       ]) => this.createFeeScheduleFormGroup(
        {
          id: this.isSettingsTab ? null : this.selectedRepresentativeFormControl.value,
          feeSchedule,
          noPetitionerBenefitCategories,
          noDerivativesBenefitCategories,
          withDerivativesBenefitCategories,
        }
      )
    );

    this.subscribers.updateFeeScheduleSubscription = this.updateFeeScheduleSubject$.pipe(
      filter(() => this.feeScheduleFormGroup.valid),
      switchMap(() => {
          if (!this.isSettingsTab) {
            return this.organizationService.updateAttorney({
              id: this.feeScheduleFormGroup.value.id,
              feeSchedule: [
                ...this.feeFormGroups.value,
                ...this.noDerivativesFeeScheduleFeeFormGroups.value,
                ...this.withDerivativesFeeScheduleFeeFormGroups.value,
              ]
            }).pipe(catchError(() => EMPTY));
          } else if (this.isSettingsTab) {
            return this.feeScheduleService.postFeeScheduleSettings(
               [
                ...this.feeFormGroups.value,
                ...this.noDerivativesFeeScheduleFeeFormGroups.value,
                ...this.withDerivativesFeeScheduleFeeFormGroups.value,
              ]
            ).pipe(catchError(() => EMPTY));
          }
        }
      ),
      filter((response) => !!response),
    ).subscribe((response) => {
      // Modal open lives here due to difference in requests for site config and representative config
      this.modalService.openConfirmModal({
        header: 'FORM.FEE_SCHEDULE.UPDATE_SUCCESS',
        body: 'FORM.FEE_SCHEDULE.UPDATE_SUCCESS_BODY',
        buttons: [OkButton],
        centered: true,
      });
    });

    this.subscribers.resetFeeScheduleSubscription = this.resetFeeScheduleSubject$.pipe(
      switchMap(() => this.feeSchedule$),
      filter((feeSchedule) => !!feeSchedule),
      withLatestFrom(
        this.noPetitionerBenefitCategories$,
        this.noDerivativesBenefitCategories$,
        this.withDerivativesBenefitCategories$,
      )
    ).subscribe((
      [
        feeSchedule,
        noPetitionerBenefitCategories,
        noDerivativesBenefitCategories,
        withDerivativesBenefitCategories,
      ]) => this.createFeeScheduleFormGroup(
      {
        id: this.isSettingsTab ? null : this.selectedRepresentativeFormControl.value,
        feeSchedule,
        noPetitionerBenefitCategories,
        noDerivativesBenefitCategories,
        withDerivativesBenefitCategories,
      }
    ));
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  createFeeScheduleFormGroup(data?: {
    id;
    feeSchedule;
    noPetitionerBenefitCategories;
    noDerivativesBenefitCategories;
    withDerivativesBenefitCategories;
  }) {
    this.feeScheduleFormGroup = new FormGroup({
      id: new FormControl(data && data.id || null),
      noPetitionerFeeSchedule: this.createFeeScheduleFormArray({
        feeSchedule: data && data.feeSchedule || null,
        allBenefitCategories: data.noPetitionerBenefitCategories,
      }),
      noDerivativesFeeSchedule: this.createFeeScheduleFormArray({
        feeSchedule: data && data.feeSchedule || null,
        allBenefitCategories: data.noDerivativesBenefitCategories,
      }),
      withDerivativesFeeSchedule: this.createFeeScheduleFormArray({
        feeSchedule: data && data.feeSchedule || null,
        allBenefitCategories: data.withDerivativesBenefitCategories,
      }),
    });
  }

  createFeeScheduleFormArray(feeSchedule: { feeSchedule; allBenefitCategories }) {
    const feeControlsArray = feeSchedule.allBenefitCategories.map((currentBenefitCategory) => {
      const foundedBenefitCategory = feeSchedule.feeSchedule.find((fee) => currentBenefitCategory.value === fee.benefitCategory);
      return this.createFeeFormGroup(
        {
          ...foundedBenefitCategory,
          group: currentBenefitCategory.benefitGroup,
          benefitCategory: currentBenefitCategory.value,
          label: currentBenefitCategory.label,
          fullLabel: currentBenefitCategory.fullLabel,
          note: currentBenefitCategory.note,
          disabled: currentBenefitCategory.disabled,
        }
      );
    });
    return new FormArray(feeControlsArray);
  }

  createFeeFormGroup(fee: FeeSchedule & { group: string; label: string; fullLabel: string; disabled: boolean; note: string }) {
    return new FormGroup({
      benefitCategory: new FormControl(fee && fee.benefitCategory || null),
      amount: new FormControl(fee && fee.amount || 0, Validators.min(0)),
      id: new FormControl(fee && fee.id || null),
      group: new FormControl(fee && fee.group || null),
      label: new FormControl(fee && fee.label || null),
      fullLabel: new FormControl(fee && fee.fullLabel || null),
      note: new FormControl(fee && fee.note || null),
      disabled: new FormControl(fee && fee.disabled || false),
    });
  }

  saveFeeSchedule() {
    this.updateFeeScheduleSubject$.next(true);
  }

  resetFeeSchedule() {
    this.resetFeeScheduleSubject$.next(true);
  }

  onKeyPressHandler(e) {
    const code = e.keyCode;
    return (code > 47 && code < 58);
  }
}
