import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../shared/shared.module';
import { MaskModule } from '../../../../shared/modules/mask.module';

import { ApplicantTypePipeModule } from '../../../pipes/applicantTypePipe.module';
import { BillingInfoComponent } from './billing-info.component';
import { BillingInfoTableRowComponent } from './billing-info-table-row/billing-info-table-row.component';
import { NgxCurrencyModule } from 'ngx-currency';

@NgModule({
  imports: [
    SharedModule,
    ApplicantTypePipeModule,
    MaskModule,
    NgxCurrencyModule,
  ],
  declarations: [
    BillingInfoComponent,
    BillingInfoTableRowComponent,
  ],
  exports: [
    BillingInfoComponent,
  ]
})
export class BillingInfoModule {
}
