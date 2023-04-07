import { Component, Input } from '@angular/core';
import { FormArray, FormControl, FormGroup } from '@angular/forms';

import { CreateApplicantFormGroupService } from '../../../services';
import { ApplicantType } from '../../../../../core/models/applicantType.enum';

@Component({
  selector: 'app-billing-info-table-row',
  templateUrl: './billing-info-table-row.component.html',
  styleUrls: ['./billing-info-table-row.component.scss'],
})

export class BillingInfoTableRowComponent {
  @Input() applicantFormGroup: FormGroup;
  @Input() applicantFormControl: FormControl;
  @Input() index = null;
  ApplicantType = ApplicantType;

  constructor(
    private createApplicantFormGroupService: CreateApplicantFormGroupService,
  ) {

  }

  get applicantsFormArray() {
    return this.createApplicantFormGroupService.formGroup.get('applicants') as FormArray;
  }
}
