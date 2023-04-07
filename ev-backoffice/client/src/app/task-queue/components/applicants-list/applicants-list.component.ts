import { Component, Input, OnInit } from '@angular/core';

import { EstimatedTax } from '../../../core/models/estimated-tax.model';
import { Package } from '../../../core/models/package/package.model';
import { FeeScheduleService } from '../../../core/services';
import { filter, map } from 'rxjs/operators';
import { combineLatest, Observable, ReplaySubject } from 'rxjs';
import { FeeSchedule } from '../../../core/models/fee-schedule.model';
import { ApplicantType } from '../../../core/models/applicantType.enum';

interface ParsedApplicant {
  applicantType: string;
  name: string;
  benefitCategory: string;
}

@Component({
  selector: 'app-applicants-list',
  templateUrl: './applicants-list.component.html',
  styleUrls: [ './applicants-list.component.scss' ]
})
export class ApplicantsListComponent implements OnInit {
  @Input()
  set currentPackage(value: Package) {
    this.package$.next(value);
  }

  @Input() showPrice = false;
  @Input() scrollable = true;
  @Input() tax: EstimatedTax = null;

  package$ = new ReplaySubject<Package>(1);
  applicantRows$: Observable<Array<ParsedApplicant & { value: number }>>;
  applicants$: Observable<ParsedApplicant[]>;

  constructor(
    private feeScheduleService: FeeScheduleService,
  ) {
  }

  ngOnInit() {
    this.applicants$ = this.package$.pipe(
      map((currentPackage) =>
        currentPackage.applicants.map(({ applicantType, profile, benefitCategory }) => ({
          applicantType,
          benefitCategory,
          name: `${profile.firstName} ${profile.lastName}`
        }))
      ),
    );

    this.applicantRows$ = combineLatest([
      this.applicants$,
      this.feeScheduleService.feeScheduleSettings$
    ]).pipe(
      filter(([ applicants, settings ]) => !!settings),
      map(([ applicants, settings ]: [ Array<ParsedApplicant>, any ]) => applicants.map((applicant) => {
          if (!applicant.benefitCategory) {
            return {
              ...applicant,
              value: 0,
            };
          }
          const category: FeeSchedule = (settings as FeeSchedule[]).find((item) => applicant.benefitCategory === item.benefitCategory);
          return {
            ...applicant,
            value: category ? category.amount : 0,
          };
        }))
    );
  }

  get taxRows() {
    return [
      { label: 'Credit: ', value: this.tax.credit },
      { label: 'Subtotal: ', value: this.tax.subTotal },
      { label: 'Estimated Tax: ', value: this.tax.estTax },
      { label: 'Grand Total: ', value: this.tax.grandTotal },
    ];
  }

  isDerivativeApplicant(row) {
    return row.applicantType === ApplicantType.DERIVATIVE_BENEFICIARY;
  }
}
