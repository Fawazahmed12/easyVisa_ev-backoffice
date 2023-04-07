import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../shared/shared.module';
import { SelectPackageModule } from '../../../../components/select-package/select-package.module';
import { PackageStatusPipeModule } from '../../../../shared/pipes/package-status/package-status-pipe.module';

import { SelectPackageTypeComponent } from './select-package-type.component';
import { AccessDeniedModalComponent } from './access-denied-modal/access-denied-modal.component';

@NgModule({
  imports: [
    SharedModule,
    SelectPackageModule,
    PackageStatusPipeModule,
  ],
  declarations: [
    AccessDeniedModalComponent,
    SelectPackageTypeComponent,
  ],
  exports: [
    SelectPackageTypeComponent,
  ]
})
export class SelectPackageTypeModule {
}
