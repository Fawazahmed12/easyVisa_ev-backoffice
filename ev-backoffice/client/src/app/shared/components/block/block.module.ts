import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TranslateModule } from '@ngx-translate/core';

import { BlockComponent } from './block.component';

@NgModule({
  imports: [
    CommonModule,
    TranslateModule,
  ],
  declarations: [
    BlockComponent,
  ],
  exports: [
    BlockComponent,
  ]
})
export class BlockModule {
}
