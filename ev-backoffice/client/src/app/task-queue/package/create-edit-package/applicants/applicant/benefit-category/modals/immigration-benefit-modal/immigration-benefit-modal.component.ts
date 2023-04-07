import { Component, Input, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { filter } from 'lodash-es';

import { map, withLatestFrom } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { benefitCategories, BenefitCategoryConst } from '../../../../../../../../core/models/benefit-categories';
import { ConfigDataService, OrganizationService } from '../../../../../../../../core/services';
import { CreateApplicantFormGroupService } from '../../../../../../services';
import { FeeSchedule } from '../../../../../../../../core/models/fee-schedule.model';
import { BenefitCategoryModel, BenefitGroupModel, Benefits } from '../../../../../../../../core/models/benefits.model';
import { ApplicantType } from '../../../../../../../../core/models/applicantType.enum';
import { PackageApplicant } from '../../../../../../../../core/models/package/package-applicant.model';
import { PetitionerStatus } from '../../../../../../../../core/models/petitioner-status.enum';
import { BenefitCategories } from '../../../../../../../../core/models/benefit-categories.enum';

export interface ImmigrationBenefitGroup {
  group: {
    value?: string;
    label: string;
    note?: string;
    shortName?: string;
  };
  categories: BenefitCategoryConst[];
}

@Component({
  selector: 'app-immigration-benefit-modal',
  templateUrl: './immigration-benefit-modal.component.html',
  styleUrls: ['./immigration-benefit-modal.component.scss'],
})

export class ImmigrationBenefitModalComponent implements OnInit {
  @Input() benefitCategory;
  @Input() applicantType: ApplicantType;
  benefitCategoryControl: FormControl = new FormControl();
  benefitCategories = benefitCategories;

  benefitGroups$: Observable<ImmigrationBenefitGroup[]>;
  benefitGroupsWithoutDerivatives$: Observable<ImmigrationBenefitGroup[]>;
  benefitGroupsDerivatives$: Observable<ImmigrationBenefitGroup[]>;
  noDerivativesBenefitCategories$: Observable<BenefitCategoryModel[]>;
  withDerivativesBenefitCategories$: Observable<BenefitCategoryModel[]>;
  benefitGroupsNoDerivatives$: Observable<BenefitGroupModel[]>;
  benefitGroupsWithDerivatives$: Observable<BenefitGroupModel[]>;

  private feeSchedule$: Observable<FeeSchedule[]>;
  ApplicantType = ApplicantType;

  constructor(
    private organizationService: OrganizationService,
    private activeModal: NgbActiveModal,
    private createApplicantFormGroupService: CreateApplicantFormGroupService,
    private configDataService: ConfigDataService,
  ) {

  }

  get representativeId() {
    return this.createApplicantFormGroupService.formGroup.get('representativeId').value;
  }

  get applicants(): PackageApplicant[] {
    return this.createApplicantFormGroupService.formGroup.get('applicants').value;
  }

  ngOnInit() {
    this.feeSchedule$ = this.organizationService.currentRepresentativeFeeSchedule$;
    this.noDerivativesBenefitCategories$ = this.configDataService.noDerivativesBenefitCategories$;
    this.withDerivativesBenefitCategories$ = this.configDataService.withDerivativesBenefitCategories$;
    this.benefitGroupsWithDerivatives$ = this.configDataService.benefitGroupsWithDerivatives$;
    this.benefitGroupsNoDerivatives$ = this.configDataService.benefitGroupsNoDerivatives$;

    this.benefitGroups$ = this.organizationService.feeScheduleEntities$.pipe(
      map(feeScheduleEntities => feeScheduleEntities[this.representativeId]),
      withLatestFrom(
        this.noDerivativesBenefitCategories$,
        this.withDerivativesBenefitCategories$,
        this.benefitGroupsWithDerivatives$,
        this.benefitGroupsNoDerivatives$,
      ),
      map((
        [
          feeSchedule,
          noDerivativesBenefitCategories,
          withDerivativesBenefitCategories,
          benefitGroupsWithDerivatives,
          benefitGroupsNoDerivatives
        ]: [
          FeeSchedule[],
          BenefitCategoryModel[],
          BenefitCategoryModel[],
          BenefitGroupModel[],
          BenefitGroupModel[]
        ]) => {
        const usualBenefitCategories = [...noDerivativesBenefitCategories, ...withDerivativesBenefitCategories];
        const usualBenefitGroups = [...benefitGroupsWithDerivatives, ...benefitGroupsNoDerivatives];
        const updatedBenefitCategories = usualBenefitCategories.map(
          (benefitCategory) => {
            const foundedBenefitCategory = feeSchedule.find((fee) => benefitCategory.value === fee.benefitCategory);
            return {
              ...benefitCategory,
              price: foundedBenefitCategory ? foundedBenefitCategory.amount : 0,
            };
          });

        return usualBenefitGroups.map(usualBenefitGroup => (
          {
            group: {
              value: usualBenefitGroup.value,
              label: usualBenefitGroup.label,
              note: usualBenefitGroup.note,
            },
            categories: filter(updatedBenefitCategories, {benefitGroup: usualBenefitGroup.value})
          }));
      })
    );

    this.benefitGroupsWithoutDerivatives$ = this.benefitGroups$.pipe(
      withLatestFrom(
        this.benefitGroupsNoDerivatives$,
        this.configDataService.benefits$
      ),
      map(([benefitGroups, benefitGroupsNoDerivatives, benefits]) => {
        const benefitGroupsWithoutDerivatives = benefitGroups.filter(
          benefitGroup => benefitGroupsNoDerivatives.some(
            benefitGroupNoDerivatives => benefitGroupNoDerivatives.value === benefitGroup.group.value)
        );

        return this.updateBenefitCategoriesDisabling(benefitGroupsWithoutDerivatives, benefits);
      })
    );

    this.benefitGroupsDerivatives$ = this.benefitGroups$.pipe(
      withLatestFrom(
        this.benefitGroupsWithDerivatives$,
        this.configDataService.benefits$
      ),
      map(([benefitGroups, benefitGroupsWithDerivatives, benefits]) => {
        const benefitGroupsDerivatives = benefitGroups.filter(
          benefitGroup => benefitGroupsWithDerivatives.some(
            benefitGroupNoDerivatives => benefitGroupNoDerivatives.value === benefitGroup.group.value)
        );

        return this.updateBenefitCategoriesDisabling(benefitGroupsDerivatives, benefits);
      })
    );

    this.benefitCategoryControl.patchValue(this.benefitCategory);
  }

  modalDismiss() {
    this.activeModal.dismiss();
  }

  modalClose() {
    this.activeModal.close(this.benefitCategoryControl.value);
  }

  // TODO hardcoded functionality for disabling der beneficiary categories

  private updateDerivativeBenefitCategoriesDisabling(benefitCategory) {
    const principalBeneficiary = this.applicants.find(applicant => applicant.applicantType === ApplicantType.PRINCIPAL_BENEFICIARY);
    const principalBeneficiaryCategory = principalBeneficiary?.benefitCategory;

    if (principalBeneficiaryCategory === BenefitCategories.K1K3) {
      return benefitCategory.value === BenefitCategories.K2K4 ? benefitCategory : {
        ...benefitCategory,
        disabled: true
      };
    } else {
      return benefitCategory.value === principalBeneficiaryCategory ? benefitCategory : {
        ...benefitCategory,
        disabled: true
      };
    }
  }

  private updateBenefitCategoriesDisabling(benefitGroups: ImmigrationBenefitGroup[], benefits: Benefits): ImmigrationBenefitGroup[] {
    const [firstApplicant, ] = this.applicants;
    const petitionerStatus: PetitionerStatus = firstApplicant.citizenshipStatus;
    if (this.applicantType === ApplicantType.DERIVATIVE_BENEFICIARY) {
      return benefitGroups.map((benefitGroup) => {
        switch (petitionerStatus) {
          case PetitionerStatus.U_S_CITIZEN: {
            return {
              ...benefitGroup,
              categories: benefitGroup.categories.map((benefitCategory) => {
                const foundedBenefitCategory = benefits?.disabledUSDerivativeCategories?.find(
                  (disabledCategory) => benefitCategory.value === disabledCategory
                );
                return this.updateDerivativeBenefitCategoriesDisabling({
                  ...benefitCategory,
                  disabled: !!foundedBenefitCategory,
                });
              })
            };
          }
          case PetitionerStatus.LPR: {
            return {
              ...benefitGroup,
              categories: benefitGroup.categories.map((benefitCategory) => {
                const foundedBenefitCategory = benefits?.disabledLPRDerivativeCategories?.find(
                  (disabledCategory) => benefitCategory.value === disabledCategory
                );
                return this.updateDerivativeBenefitCategoriesDisabling({
                  ...benefitCategory,
                  disabled: !!foundedBenefitCategory,
                });
              })
            };
          }
          case PetitionerStatus.ALIEN: {
            return {
              ...benefitGroup,
              categories: benefitGroup.categories.map((benefitCategory) => ({
                  ...benefitCategory,
                  disabled: true,
                }))
            };
          }
          default: {
            return benefitGroup;
          }
        }
      });
    }

    return benefitGroups.map((benefitGroup) => {
      switch (petitionerStatus) {
        case PetitionerStatus.U_S_CITIZEN: {
          return {
            ...benefitGroup,
            categories: benefitGroup.categories.map((benefitCategory) => {
              const foundedBenefitCategory = benefits.disabledUSCitizenCategories
              .find((disabledCategory) => benefitCategory.value === disabledCategory);
              return {
                ...benefitCategory,
                disabled: !!foundedBenefitCategory,
              };
            })
          };
        }
        case PetitionerStatus.U_S_NATIONAL:
        case PetitionerStatus.LPR: {
          return {
            ...benefitGroup,
            categories: benefitGroup.categories.map((benefitCategory) => {
              const foundedBenefitCategory = benefits.disabledLPRCategories
              .find((disabledCategory) => benefitCategory.value === disabledCategory);
              return {
                ...benefitCategory,
                disabled: !!foundedBenefitCategory,
              };
            })
          };
        }
        case PetitionerStatus.ALIEN: {
          return {
            ...benefitGroup,
            categories: benefitGroup.categories.map((benefitCategory) => ({
                ...benefitCategory,
                disabled: true,
              }))
          };
        }
        default: {
          return benefitGroup;
        }
      }
    });
  }
}
