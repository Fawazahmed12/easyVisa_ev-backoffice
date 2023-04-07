import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ModalDismissReasons, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { PackageStatus } from '../../../../../core/models/package/package-status.enum';
import { DATE_PATTERN } from '../../../../../shared/validators/constants/date-pattern.const';
import { startEndDateValidator } from '../../../../../shared/validators/start-end-date.validator';

@Component({
  selector: 'app-delete-old-leads-modal',
  templateUrl: 'delete-old-leads-modal.component.html',
})
export class DeleteOldLeadsModalComponent implements OnInit {

  dateFiltersFormGroup: FormGroup;

  get startDateControl() {
    return this.dateFiltersFormGroup.get('startDate');
  }
  get endDateControl() {
    return this.dateFiltersFormGroup.get('endDate');
  }
  get controlsErrors() {
    return this.startDateControl.hasError('pattern')
      && this.startDateControl.hasError('ngbDate')
      && this.endDateControl.hasError('pattern')
      && this.endDateControl.hasError('ngbDate');
  }

  constructor(
    private activeModal: NgbActiveModal,
  ) {
  }

  ngOnInit() {
    this.dateFiltersFormGroup = new FormGroup(
      {
        startDate: new FormControl(null, {
          validators: [
            Validators.pattern(DATE_PATTERN),
            Validators.required,
          ],
        }),
        endDate: new FormControl(null, {
          validators: [
            Validators.pattern(DATE_PATTERN),
            Validators.required,
          ],
        }),
      }, {
        validators: [startEndDateValidator('startDate', 'endDate')],
      },
    );
  }

  dismissModal() {
    this.activeModal.dismiss(ModalDismissReasons.ESC);
  }

  onSubmit() {
    const data = {
      startDate: this.startDateControl.value,
      endDate: this.endDateControl.value,
      status: PackageStatus.LEAD
    };
    this.activeModal.close(data);
  }
}
