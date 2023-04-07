import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';

import { AccountAdjustmentRefundsComponent } from './account-adjustment-refunds.component';


@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [AccountAdjustmentRefundsComponent],
  exports: [
    AccountAdjustmentRefundsComponent
  ]
})
export class AccountAdjustmentRefundsModule {
}
