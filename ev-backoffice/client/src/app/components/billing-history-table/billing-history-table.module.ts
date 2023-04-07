import { NgModule } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { NgbPaginationModule } from '@ng-bootstrap/ng-bootstrap';

import { SharedModule } from '../../shared/shared.module';

import { TableModule } from '../table/table.module';
import { SpinnerModule } from '../spinner/spinner.module';

import { BillingHistoryTableComponent } from './billing-history-table.component';


@NgModule({
  imports: [
    SharedModule,
    TableModule,
    NgbPaginationModule,
    SpinnerModule,
  ],
  declarations: [
    BillingHistoryTableComponent
  ],
  exports: [
    BillingHistoryTableComponent
  ],
  providers: [
    DatePipe,
    CurrencyPipe
  ]
})
export class BillingHistoryTableModule {
}
