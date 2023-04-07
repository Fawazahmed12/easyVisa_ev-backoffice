import { Component, Input } from '@angular/core';

import { EstimatedTax } from '../../core/models/estimated-tax.model';


@Component({
  selector: 'app-taxes-info',
  templateUrl: './taxes-info.component.html',
})


export class TaxesInfoComponent {
  @Input() tax: EstimatedTax;
  @Input() feeLabel: string;
  @Input() showCredit = true;

  constructor(
  ) {
  }
}
