import { Component, Input } from '@angular/core';

import { InfoDetails } from '../../models/info-details.model';


@Component({
  selector: 'app-time-frame',
  templateUrl: './time-frame.component.html',
})
export class TimeFrameComponent {
  @Input() title: string;
  @Input() indicators: InfoDetails;
  @Input() isFinancial = false;
}
