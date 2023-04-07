import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { PackageStatusPipeModule } from '../../shared/pipes/package-status/package-status-pipe.module';

import { SelectPackageComponent } from './select-package.component';

@NgModule({
  imports: [
    SharedModule,
    PackageStatusPipeModule,
  ],
  declarations: [
    SelectPackageComponent,
  ],
  exports: [
    SelectPackageComponent,
  ],
})
export class SelectPackageModule { }
