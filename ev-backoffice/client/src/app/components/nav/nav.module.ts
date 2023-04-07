import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { NavComponent } from './nav.component';
import { SharedModule } from '../../shared/shared.module';


@NgModule({
  imports: [
    CommonModule,
    SharedModule,
  ],
  declarations: [
    NavComponent,
  ],
  exports: [
    NavComponent,
  ]
})
export class NavModule {
}
