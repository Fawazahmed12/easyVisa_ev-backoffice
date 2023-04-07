import { Component, Input } from '@angular/core';

import { FormGroup } from '@angular/forms';


@Component({
  selector: 'app-rank-unit',
  templateUrl: './rank-unit.component.html',
})
export class RankUnitComponent {
  @Input() title: string;
  @Input() form: FormGroup;

  get pointsFormControl() {
    return this.form.get('points');
  }

  get articlesInMonthFormControl() {
    return this.form.get('articlesInMonth');
  }

  get articlesInQuarterFormControl() {
    return this.form.get('articlesInQuarter');
  }

  get articlesInHalfFormControl() {
    return this.form.get('articlesInHalf');
  }
}
