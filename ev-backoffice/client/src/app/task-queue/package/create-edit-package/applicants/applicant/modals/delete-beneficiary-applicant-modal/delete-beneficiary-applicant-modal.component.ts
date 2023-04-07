import { Component, Input } from '@angular/core';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-delete-beneficiary-applicant-modal',
  templateUrl: './delete-beneficiary-applicant-modal.component.html',
})

export class DeleteBeneficiaryApplicantModalComponent {
  @Input() packageTypeFormControl: FormControl;
}
