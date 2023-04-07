import { Component, Input } from '@angular/core';

import { governmentFeesConst } from '../../../../core/models/government-fees';

@Component({
  selector: 'app-government-fees-modal',
  templateUrl: './government-fees-modal.component.html',
  styles: [`
    .font-18{
      font-size:18px;
    }
  `]
})

export class GovernmentFeesModalComponent {
  @Input() governmentFee;

  governmentFeesConst = governmentFeesConst;
  uscisGovLink = 'https://www.uscis.gov/forms/our-fees';

  originalOrder = () => null;
}
