import { NgModule } from '@angular/core';

import { NgbModalModule } from '@ng-bootstrap/ng-bootstrap';

import { SharedModule } from '../../shared/shared.module';

import { ModalHeaderComponent } from './modal-header.component';


@NgModule({
  imports: [
    SharedModule,
    NgbModalModule,
  ],
  declarations: [
    ModalHeaderComponent,
  ],
  exports: [
    ModalHeaderComponent,
  ],
})

export class ModalHeaderModule {
}
