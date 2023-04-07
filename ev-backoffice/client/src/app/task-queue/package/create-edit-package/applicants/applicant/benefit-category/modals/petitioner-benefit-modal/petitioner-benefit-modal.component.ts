import { Component, Input, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { map, withLatestFrom } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { filter as _filter } from 'lodash-es';

import { ImmigrationBenefitGroup } from '../immigration-benefit-modal/immigration-benefit-modal.component';

import { benefitCategories } from '../../../../../../../../core/models/benefit-categories';
import { FeeSchedule } from '../../../../../../../../core/models/fee-schedule.model';
import { ConfigDataService, OrganizationService } from '../../../../../../../../core/services';
import { ApplicantType } from '../../../../../../../../core/models/applicantType.enum';
import { BenefitCategoryModel, BenefitGroupModel } from '../../../../../../../../core/models/benefits.model';
import { CitizenshipStatus } from '../../../../../../../../core/models/citizenship-status.enum';
import { CreateApplicantFormGroupService } from '../../../../../../services';

@Component({
  selector: 'app-petitioner-benefit-modal',
  templateUrl: './petitioner-benefit-modal.component.html',
})

export class PetitionerBenefitModalComponent implements OnInit {
  @Input() benefitCategory;
  @Input() citizenshipStatus: CitizenshipStatus;
  benefitCategoryControl: FormControl = new FormControl();
  benefitCategories = benefitCategories;
  ApplicantType = ApplicantType;

  selfPetitionerBenefitGroups$: Observable<ImmigrationBenefitGroup[]>;
  noPetitionerBenefitCategories$: Observable<BenefitCategoryModel[]>;
  noPetitionerBenefitGroups$: Observable<BenefitGroupModel[]>;

  get isPetitionerOptionDisabled(): boolean {
    return this.citizenshipStatus === CitizenshipStatus.ALIEN;
  }

  get representativeIdFormControl() {
    return this.createApplicantFormGroupService.formGroup.get('representativeId');
  }

  private feeSchedule$: Observable<FeeSchedule[]>;

  constructor(
    private activeModal: NgbActiveModal,
    private organizationService: OrganizationService,
    private configDataService: ConfigDataService,
    private createApplicantFormGroupService: CreateApplicantFormGroupService,
  ) {

  }

  ngOnInit() {
    this.noPetitionerBenefitGroups$ = this.configDataService.noPetitionerBenefitGroups$;
    this.noPetitionerBenefitCategories$ = this.configDataService.getUpdatedNoPetitionerBenefitCategories(this.citizenshipStatus);
    this.feeSchedule$ = this.organizationService.currentRepresentativeFeeSchedule$;
    this.benefitCategoryControl.patchValue(this.benefitCategory);

    this.selfPetitionerBenefitGroups$ = this.organizationService.feeScheduleEntities$.pipe(
      map(feeScheduleEntities => feeScheduleEntities[this.representativeIdFormControl.value]),
      withLatestFrom(
        this.noPetitionerBenefitCategories$,
        this.noPetitionerBenefitGroups$,
      ),
      map((
        [
          feeSchedule,
          noPetitionerBenefitCategories,
          noPetitionerBenefitGroups
        ]: [
          FeeSchedule[],
          BenefitCategoryModel[],
          BenefitGroupModel[]
        ]) => {
        const updatedBenefitCategories = noPetitionerBenefitCategories.map(
          (benefitCategory) => {
            const foundedBenefitCategory = feeSchedule.find((fee) => benefitCategory.value === fee.benefitCategory);
            return {
              ...benefitCategory,
              price: foundedBenefitCategory ? foundedBenefitCategory.amount : 0
            };
          });

        return noPetitionerBenefitGroups.map(usualBenefitGroup => (
          {
            group: {
              value: usualBenefitGroup.value,
              label: usualBenefitGroup.label,
            },
            categories: _filter(updatedBenefitCategories, {benefitGroup: usualBenefitGroup.value})
          }));
      })
    );
  }

  modalDismiss() {
    this.activeModal.dismiss();
  }

  modalClose() {
    this.activeModal.close(this.benefitCategoryControl.value);
  }
}
