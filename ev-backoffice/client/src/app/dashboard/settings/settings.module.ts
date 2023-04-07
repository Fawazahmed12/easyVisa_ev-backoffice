import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../../shared/shared.module';
import { FeeScheduleModule } from '../../components/fee-schedule/fee-schedule.module';

import { NgrxDashboardSettingsModule } from './ngrx/module';
import { DashboardSettingsService } from './settings.service';
import { SettingsRoutingModule } from './settings-routing.module';
import { SettingsComponent } from './settings.component';

import { CustomerFeesDiscountsComponent } from './customer-fees-discounts/customer-fees-discounts.component';
import { GovernmentFeesModule } from './government-fees/government-fees.module';
import { SearchResultPanelModule } from './search-result-panel/search-result-panel.module';
import { BatchJobsResolverService } from './resolvers/batch-jobs.resolver.service';
import { RankingDataResolverService } from './resolvers/ranking-data.resolver.service';
import { RepresentativesCountResolverService } from './resolvers/representatives-count.resolver.service';
import { RepresentativesCountModule } from './representatives-count/representatives-count.module';
import { SiteJobsModule } from './site-jobs/site-jobs.module';

@NgModule({
  imports: [
    CommonModule,
    SettingsRoutingModule,
    NgrxDashboardSettingsModule,
    SharedModule,
    GovernmentFeesModule,
    FeeScheduleModule,
    SearchResultPanelModule,
    RepresentativesCountModule,
    SiteJobsModule,
  ],
  declarations: [
    SettingsComponent,
    CustomerFeesDiscountsComponent,
  ],
  providers: [
    DashboardSettingsService,
    RankingDataResolverService,
    RepresentativesCountResolverService,
    BatchJobsResolverService,
  ]
})
export class SettingsModule {
}
