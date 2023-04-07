import { Component, Input } from '@angular/core';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-time-period',
  templateUrl: './time-period.component.html',
})
export class TimePeriodComponent {
  @Input() startDateControl: FormControl;
  @Input() endDateControl: FormControl;
}
