import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { BillingHistoryTableModule } from '../../../components/billing-history-table/billing-history-table.module';
import { HasRoleDirectiveModule } from '../../../shared/directives/has-role/has-role-directive.module';

import { SelectAffiliationModule } from '../../components/select-affiliation/select-affiliation.module';
import { TimeFrameModule } from '../../components/time-frame/time-frame.module';
import { ArticleBonusesModule } from '../../modals/article-bonuses/article-bonuses.module';

import { CreateCreditModule } from '../create-credit/create-credit.module';
import { AccountAdjustmentRefundsModule } from '../account-adjustment-refunds/account-adjustment-refunds.module';
import { FinancialMainComponent } from './financial-main.component';
import { FinancialMainRoutingModule } from './financial-main-routing.module';

@NgModule({
  imports: [
    SharedModule,
    FinancialMainRoutingModule,
    AccountAdjustmentRefundsModule,
    CreateCreditModule,
    BillingHistoryTableModule,
    HasRoleDirectiveModule,
    SelectAffiliationModule,
    TimeFrameModule,
    ArticleBonusesModule,
  ],
  declarations: [ FinancialMainComponent ],
})
export class FinancialMainModule {
}
