import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { BlockModule } from '../../../shared/components/block/block.module';

import { GovernmentFeesComponent } from './government-fees.component';

@NgModule({
  imports: [
    SharedModule,
    BlockModule,
  ],
  declarations: [
    GovernmentFeesComponent,
  ],
  exports: [
    GovernmentFeesComponent,
  ]
})
export class GovernmentFeesModule {
}
