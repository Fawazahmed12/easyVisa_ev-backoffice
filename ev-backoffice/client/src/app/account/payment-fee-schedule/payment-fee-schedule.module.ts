import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { SelectNameModule } from '../../components/select-name/select-name.module';
import { BillingHistoryTableModule } from '../../components/billing-history-table/billing-history-table.module';
import { IfActiveUserDirectiveModule } from '../../shared/directives/if-active-user/if-active-user-directive.module';
import { FeeScheduleModule } from '../../components/fee-schedule/fee-schedule.module';

import { PaymentFeeScheduleRoutingModule } from './payment-fee-schedule-routing.module';
import { PaymentFeeScheduleComponent } from './payment-fee-schedule.component';
import { BalanceCreditModule } from './balance-credit/balance-credit.module';
import { PaymentMethodModule } from './payment-method/payment-method.module';
import { WarningPersonalPageModule } from '../../components/warning-personal-page/warning-personal-page.module';

@NgModule({
  imports: [
    SharedModule,
    PaymentFeeScheduleRoutingModule,
    BalanceCreditModule,
    PaymentMethodModule,
    SelectNameModule,
    BillingHistoryTableModule,
    IfActiveUserDirectiveModule,
    FeeScheduleModule,
    WarningPersonalPageModule,
  ],
  declarations: [
    PaymentFeeScheduleComponent,
  ]
})
export class PaymentFeeScheduleModule {
}
