import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { BlockModule } from '../../../shared/components/block/block.module';

import { RepresentativesCountComponent } from './representatives-count.component';


@NgModule({
  imports: [
    SharedModule,
    BlockModule,
  ],
  declarations: [
    RepresentativesCountComponent,
  ],
  exports: [
    RepresentativesCountComponent,
  ]
})
export class RepresentativesCountModule {
}
