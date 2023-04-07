import { Component, Input } from '@angular/core';

import { ProgressStatus } from '../models/progress-status.model';


@Component({
  selector: 'app-progress-unit',
  templateUrl: './progress-unit.component.html',
  styleUrls: ['./progress-unit.component.scss']
})

export class ProgressUnitComponent {
  @Input() progress: ProgressStatus;

  getPercentComplete(item) {
    return !!item?.percentComplete ? item.percentComplete : 0;
  }
}
