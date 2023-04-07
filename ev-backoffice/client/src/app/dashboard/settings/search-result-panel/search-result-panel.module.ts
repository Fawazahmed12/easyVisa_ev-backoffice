import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { BlockModule } from '../../../shared/components/block/block.module';

import { RankUnitModule } from '../components/rank-unit/rank-unit.module';

import { SearchResultPanelComponent } from './search-result-panel.component';


@NgModule({
  imports: [
    SharedModule,
    BlockModule,
    RankUnitModule,
  ],
  declarations: [
    SearchResultPanelComponent,
  ],
  exports: [
    SearchResultPanelComponent,
  ]
})
export class SearchResultPanelModule {
}
