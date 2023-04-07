import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';
import { TabsModule } from '../shared/components/tabs/tabs.module';

import { AccountComponent } from './account.component';
import { AccountRoutingModule } from './account-routing.module';
import { NgrxAccountModule } from './ngrx/module';
import { MandatoryAdminPositionModule } from './modals/mandatory-admin-position/mandatory-admin-position.module';


@NgModule({
  imports: [
    CommonModule,
    AccountRoutingModule,
    NgrxAccountModule,
    SharedModule,
    TabsModule,
    MandatoryAdminPositionModule,
  ],
  declarations: [
    AccountComponent,
  ],
})
export class AccountModule { }
