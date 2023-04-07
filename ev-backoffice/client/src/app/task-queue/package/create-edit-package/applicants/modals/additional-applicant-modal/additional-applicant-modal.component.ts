import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-additional-applicant-modal',
  templateUrl: './additional-applicant-modal.component.html',
})

export class AdditionalApplicantModalComponent {
  @Input() applicantFee: number;
}
