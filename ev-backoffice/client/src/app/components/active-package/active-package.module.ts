import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../../shared/shared.module';

import { ActivePackageComponent } from './active-package.component';


@NgModule({
  imports: [
    CommonModule,
    SharedModule,
  ],
  declarations: [
    ActivePackageComponent,
  ],
  exports: [
    ActivePackageComponent,
  ],
  entryComponents: [
    ActivePackageComponent,
  ]
})
export class ActivePackageModule { }
