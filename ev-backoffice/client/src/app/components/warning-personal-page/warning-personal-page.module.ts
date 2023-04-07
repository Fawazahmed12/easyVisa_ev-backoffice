import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../../shared/shared.module';
import { WarningPersonalPageComponent } from './warning-personal-page.component';


@NgModule({
  imports: [
    SharedModule,
    CommonModule,
  ],
  declarations: [
    WarningPersonalPageComponent,
  ],
  exports: [
    WarningPersonalPageComponent,
  ]
})
export class WarningPersonalPageModule { }
