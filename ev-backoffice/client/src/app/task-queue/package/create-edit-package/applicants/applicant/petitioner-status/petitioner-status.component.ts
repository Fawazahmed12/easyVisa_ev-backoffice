import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { FormArray, FormControl } from '@angular/forms';

import { Observable } from 'rxjs';

import { ModalService } from '../../../../../../core/services';
import { citizenshipStatusValue } from '../../../../../models/citizenship-status.model';

import { CreateApplicantFormGroupService } from '../../../../services';

@Component({
  selector: 'app-petitioner-status',
  templateUrl: './petitioner-status.component.html',
  styleUrls: ['./petitioner-status.component.scss'],
})

export class PetitionerStatusComponent implements  OnInit {
  @Input() citizenshipStatusFormControl: FormControl;
  @Input() aNumberFormControl: FormControl;
  @Input() elisAccountNumberFormControl: FormControl;
  @ViewChild('statusChangeWarningModal', { static: true }) statusChangeWarningModal;

  isSubmittedFormGroup$: Observable<boolean>;
  citizenshipStatusValues = citizenshipStatusValue;

  constructor(
    private modalService: ModalService,
    private createApplicantFormGroupService: CreateApplicantFormGroupService,
  ) {

  }

  ngOnInit() {
    this.isSubmittedFormGroup$ = this.createApplicantFormGroupService.submittedSubject$;
  }

  get applicantsFormArray() {
    return this.createApplicantFormGroupService.formGroup.get('applicants') as FormArray;
  }
}
