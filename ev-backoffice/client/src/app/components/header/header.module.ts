import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HeaderComponent } from './header.component';
import { EntryModule } from '../entry/entry.module';
import { ActivePackageModule } from '../active-package/active-package.module';
import { LogoMessageModule } from '../logo-message/logo-message.module';
import { PaymentWarningModule } from '../payment-warning/payment-warning.module';


@NgModule({
  imports: [
    CommonModule,
    EntryModule,
    ActivePackageModule,
    LogoMessageModule,
    PaymentWarningModule,
  ],
  declarations: [
    HeaderComponent,
  ],
  exports: [
    HeaderComponent,
  ]
})
export class HeaderModule { }
