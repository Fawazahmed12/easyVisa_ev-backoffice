import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { ModalHeaderModule } from '../../../components/modal-header/modal-header.module';
import { SpinnerModule } from '../../../components/spinner/spinner.module';

import { RejectFileComponent } from './reject-file.component';


@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
    SpinnerModule,
  ],
  declarations: [
    RejectFileComponent,
  ],
  exports: [
    RejectFileComponent,
  ],
  entryComponents: [
    RejectFileComponent,
  ]
})
export class RejectFileModule { }
