import { Component, forwardRef, OnInit } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

import { combineLatest, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { filter as _filter } from 'lodash-es';

import { ImmigrationBenefitGroup } from '../../../../../../../package/create-edit-package/applicants/applicant/benefit-category/modals/immigration-benefit-modal/immigration-benefit-modal.component';

import { BenefitCategoryModel, BenefitGroupModel } from '../../../../../../../../core/models/benefits.model';
import { ConfigDataService } from '../../../../../../../../core/services';


@Component({
  selector: 'app-search-benefit-categories',
  templateUrl: './search-benefit-categories.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SearchBenefitCategoriesComponent),
      multi: true
    }
  ]
})

export class SearchBenefitCategoriesComponent implements OnInit, ControlValueAccessor {

  searchBenefitGroups$: Observable<ImmigrationBenefitGroup[]>;

  benefitCategoriesValue = [];

  constructor(
    private configDataService: ConfigDataService,
  ) {
  }

  ngOnInit() {
    this.searchBenefitGroups$ = combineLatest([
      this.configDataService.allBenefitCategories$,
      this.configDataService.searchGroups$,
    ]).pipe(
      map((
        [
          allBenefitCategories,
          searchGroups,
        ]: [
          BenefitCategoryModel[],
          BenefitGroupModel[],
        ]) => searchGroups.map((searchGroup: BenefitGroupModel) => (
        {
          group: {...searchGroup},
          categories: _filter(allBenefitCategories, {benefitGroup: searchGroup.value})
        }))
      ),
    );
  }


  private onChange: Function = () => {
  };
  private onTouch: Function = () => {
  };

  writeValue(value): void {
    this.benefitCategoriesValue = value || [];
  }

  registerOnChange(fn: Function): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: Function): void {
    this.onTouch = fn;
  }

  selectBenefitCheckboxes(event) {
    const {checked, value} = event.target;
    if (checked) {
      this.benefitCategoriesValue = [...this.benefitCategoriesValue, value];
    } else {
      this.benefitCategoriesValue = this.benefitCategoriesValue.filter(item => item !== value);
    }
    this.onChange(this.benefitCategoriesValue);
  }
}
