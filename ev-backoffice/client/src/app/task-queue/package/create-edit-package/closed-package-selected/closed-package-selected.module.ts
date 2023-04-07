import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../shared/shared.module';

import { ClosedPackageSelectedComponent } from './closed-package-selected.component';


@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [
    ClosedPackageSelectedComponent,
  ],
  exports: [
    ClosedPackageSelectedComponent,
  ]
})
export class ClosedPackageSelectedModule {
}
