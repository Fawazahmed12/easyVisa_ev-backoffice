import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { CardComponent } from './card.component';


@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [
    CardComponent,
  ],
  exports: [
    CardComponent,
  ]
})
export class CardModule {
}
