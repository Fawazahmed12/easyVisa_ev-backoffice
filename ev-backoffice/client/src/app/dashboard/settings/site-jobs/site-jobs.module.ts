import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { BlockModule } from '../../../shared/components/block/block.module';

import { SiteJobsComponent } from './site-jobs.component';


@NgModule({
  imports: [
    SharedModule,
    BlockModule,
  ],
  declarations: [
    SiteJobsComponent,
  ],
  exports: [
    SiteJobsComponent,
  ]
})
export class SiteJobsModule {
}
