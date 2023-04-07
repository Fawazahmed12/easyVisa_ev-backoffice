import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { NgbActiveModal, NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';

import { states } from '../../../../../../../core/models/states';

@Component({
  selector: 'app-add-bar-admission-modal',
  templateUrl: 'add-bar-admission-modal.component.html',
})
export class AddBarAdmissionModalComponent {
  @Input() licensedRegionFormGroup: FormGroup;
  @Input() minDate: NgbDateStruct;
  @Input() maxDate: NgbDateStruct;
  states = states;

  get stateFormControl() {
    return this.licensedRegionFormGroup.get('state');
  }

  get dateLicensedFormControl() {
    return this.licensedRegionFormGroup.get('dateLicensed');
  }

  constructor(
    private activeModal: NgbActiveModal,
  ) {

  }

  modalDismiss() {
    this.activeModal.dismiss();
  }

  addBarNumber() {
    this.activeModal.close(this.licensedRegionFormGroup);
  }
}
