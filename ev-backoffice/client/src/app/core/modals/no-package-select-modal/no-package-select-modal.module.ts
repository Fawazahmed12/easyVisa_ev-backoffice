import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NoPackageSelectModalComponent } from './no-package-select-modal.component';
import { SharedModule } from '../../../shared/shared.module';
import { ModalHeaderModule } from '../../../components/modal-header/modal-header.module';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    ModalHeaderModule,
  ],
  declarations: [
    NoPackageSelectModalComponent
  ],
  exports: [
    NoPackageSelectModalComponent
  ],
  entryComponents: [
    NoPackageSelectModalComponent
  ]
})
export class NoPackageSelectModalModule {
}
