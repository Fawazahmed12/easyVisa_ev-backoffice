import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';
import { TabsModule } from '../shared/components/tabs/tabs.module';

import { NgrxDashboardModule } from './ngrx/module';
import { DashboardRoutingModule } from './dashboard-routing.module';
import { DashboardComponent } from './dashboard.component';

@NgModule({
  imports: [
    CommonModule,
    DashboardRoutingModule,
    SharedModule,
    TabsModule,
    NgrxDashboardModule,
  ],
  declarations: [ DashboardComponent ],
})
export class DashboardModule {
}
