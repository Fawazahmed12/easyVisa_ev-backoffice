import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { ModalHeaderModule } from '../../../components/modal-header/modal-header.module';

import { PackageCannotBeOpenModalComponent } from './package-cannot-be-open-modal.component';


@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
  ],
  declarations: [
    PackageCannotBeOpenModalComponent,
  ],
  exports: [
    PackageCannotBeOpenModalComponent,
  ],
  entryComponents: [
    PackageCannotBeOpenModalComponent,
  ],
})

export class PackageCannotBeOpenModalModule {
}
