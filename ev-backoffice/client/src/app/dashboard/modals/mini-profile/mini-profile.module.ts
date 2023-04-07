import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';

import { MiniProfileComponent } from './mini-profile.component';

@NgModule({
  imports: [
    SharedModule
  ],
  declarations: [
    MiniProfileComponent,
  ],
  exports: [
    MiniProfileComponent,
  ]
})

export class MiniProfileModule {
}

