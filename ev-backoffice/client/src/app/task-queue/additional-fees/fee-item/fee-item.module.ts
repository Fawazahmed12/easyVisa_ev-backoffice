import { NgModule } from '@angular/core';

import { NgxCurrencyModule } from 'ngx-currency';

import { SharedModule } from '../../../shared/shared.module';

import { FeeItemComponent } from './fee-item.component';


@NgModule({
  imports: [
    SharedModule,
    NgxCurrencyModule,
  ],
  declarations: [
    FeeItemComponent,
  ],
  exports: [
    FeeItemComponent,
  ]
})
export class FeeItemModule {
}
