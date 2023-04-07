import { Component, Input } from '@angular/core';
import { FormControl } from '@angular/forms';

import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-datepicker-group',
  templateUrl: 'datepicker-group.component.html',
  styles: [`
    button.bg-light-gray:hover:disabled{
        background-color: #e9ecef !important;
    }
  `]
})
export class DatepickerGroupComponent {
  @Input() dateFormControl: FormControl;
  @Input() maxDate: NgbDateStruct;
  @Input() minDate: NgbDateStruct;
}
