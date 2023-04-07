import { Component, Input } from '@angular/core';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-delete-derivative-beneficiary-modal',
  templateUrl: './delete-derivative-beneficiary-modal.component.html',
})

export class DeleteDerivativeBeneficiaryModalComponent {
  @Input() packageTypeFormControl: FormControl;
}
