import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';

import { BalanceCreditComponent } from './balance-credit.component';
import { TaxesInfoModule } from '../../../components/taxes-info/taxes-info.module';

@NgModule({
  imports: [
    SharedModule,
    TaxesInfoModule,
  ],
  declarations: [
    BalanceCreditComponent,
  ],
  exports: [
    BalanceCreditComponent,
  ]
})
export class BalanceCreditModule {}
