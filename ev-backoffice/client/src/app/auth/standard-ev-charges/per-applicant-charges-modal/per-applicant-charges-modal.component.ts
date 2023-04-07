import { Component, Input, OnInit } from '@angular/core';
import { filter } from 'rxjs/operators';
import { Benefits } from '../../../core/models/benefits.model';
import { ConfigDataService } from '../../../core/services';

@Component({
  selector: 'app-per-applicant-charges-modal',
  templateUrl: './per-applicant-charges-modal.component.html',
  styleUrls: ['./per-applicant-charges-modal.component.scss']
})

export class PerApplicantChargesModalComponent implements OnInit {

  @Input() feeSchedules;
  perApplicantCharges: any = [];

  constructor(private configDataService: ConfigDataService) {
  }

  ngOnInit(): void {
    this.configDataService.benefits$.pipe(
      filter((benefits) => !!benefits),
    ).subscribe((benefits: Benefits) => {
      const benefitCategoriesMapper = {};
      const benefitGroupMapper = {};
      benefits.searchGroups.forEach((searchGroup) => {
        benefitGroupMapper[ searchGroup.value ] = searchGroup;
      });
      benefits.benefitCategories.forEach((benefitCategory) => {
        benefitCategoriesMapper[ benefitCategory.value ] = {
          ...benefitCategory,
          benefitGroupObj: benefitGroupMapper[ benefitCategory.benefitGroup ]
        };
      });
      this.perApplicantCharges = this.feeSchedules.map((feeSchedule) => ({
          ...feeSchedule,
          benefitCategoryObj: benefitCategoriesMapper[ feeSchedule.benefitCategory ]
        }));
    });
  }
}
