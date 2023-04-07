import { NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';

import { SharedModule } from '../../shared/shared.module';
import { AlertsResolverService } from '../../core/resolvers/alerts-resolver.service';
import { AlertsService } from '../../core/services/alerts.service';

import { TableModule } from '../table/table.module';
import { WarningPersonalPageModule } from '../warning-personal-page/warning-personal-page.module';
import { PaginationModule } from '../pagination/pagination.module';

import { AlertsRoutingModule } from './alerts-routing.module';
import { AlertsComponent } from './alerts.component';
import { CreateAlertModule } from './create-alert/create-alert.module';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    AlertsRoutingModule,
    TableModule,
    CreateAlertModule,
    WarningPersonalPageModule,
    PaginationModule,
  ],
  declarations: [
    AlertsComponent,
  ],
  providers: [
    AlertsService,
    AlertsResolverService,
    DatePipe,
  ],
})
export class AlertsModule { }
