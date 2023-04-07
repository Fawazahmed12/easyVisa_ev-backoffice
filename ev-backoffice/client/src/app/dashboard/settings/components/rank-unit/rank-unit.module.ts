import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../shared/shared.module';

import { RankUnitComponent } from './rank-unit.component';


@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [
    RankUnitComponent
  ],
  exports: [
    RankUnitComponent
  ],
  entryComponents: [
    RankUnitComponent
  ]
})
export class RankUnitModule {
}
