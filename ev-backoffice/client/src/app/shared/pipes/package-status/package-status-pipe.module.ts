import { NgModule } from '@angular/core';

import { PackageStatusPipe } from './package-status.pipe';


@NgModule({
  declarations: [
    PackageStatusPipe,
  ],
  exports: [
    PackageStatusPipe
  ]
})
export class PackageStatusPipeModule { }
