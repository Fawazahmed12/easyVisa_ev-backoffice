import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { FinancialRoutingModule } from './financial-routing.module';
import { FinancialService } from './financial.service';
import { MyAccountTransactionsResolverService } from '../../core/resolvers/my-account-transactions-resolver.service';
import { FeeDetailsResolverService } from '../../core/resolvers/fee-details-resolver.service';
import { FinancialResolverService } from './financial-resolver.service';

@NgModule({
  imports: [
    SharedModule,
    FinancialRoutingModule,
  ],
  providers: [
    FinancialService,
    MyAccountTransactionsResolverService,
    FeeDetailsResolverService,
    FinancialResolverService,
  ]
})
export class FinancialModule {
}
