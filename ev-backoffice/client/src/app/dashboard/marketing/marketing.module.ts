import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { TimeFrameModule } from '../components/time-frame/time-frame.module';
import { SelectAffiliationModule } from '../components/select-affiliation/select-affiliation.module';

import { MarketingRoutingModule } from './marketing-routing.module';
import { MarketingComponent } from './marketing.component';
import { MarketingService } from './marketing.service';
import { MarketingResolverService } from './marketing-resolver.service';


@NgModule({
  imports: [
    SharedModule,
    MarketingRoutingModule,
    TimeFrameModule,
    SelectAffiliationModule,
  ],
  declarations: [MarketingComponent],
  providers: [
    MarketingService,
    MarketingResolverService
  ],
})
export class MarketingModule {
}
