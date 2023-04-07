import { Component, OnInit } from '@angular/core';

import { Observable } from 'rxjs';
import { filter } from 'rxjs/operators';

import { ConfigDataService, TaxesService } from '../../core/services';
import { EstimatedTax } from '../../core/models/estimated-tax.model';
import { BenefitCategoryModel } from '../../core/models/benefits.model';

@Component({
  selector: 'app-additional-applicant-fee-info',
  templateUrl: './additional-applicant-fee-info.component.html',
})

export class AdditionalApplicantFeeInfoComponent implements OnInit {
  estimatedTax$: Observable<EstimatedTax>;
  allBenefitCategories$: Observable<BenefitCategoryModel[]>;

  constructor(
    private taxesService: TaxesService,
    private configDataService: ConfigDataService,
  ) {
  }

  ngOnInit() {
    this.estimatedTax$ = this.taxesService.packageChangingStatusFeeWithTax$;
    this.allBenefitCategories$ = this.configDataService.allBenefitCategories$.pipe(
      filter(allBenefitCategories => !!allBenefitCategories)
    );
  }
}
