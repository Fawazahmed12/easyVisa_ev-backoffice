import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { ModalHeaderModule } from '../../../components/modal-header/modal-header.module';
import { SpinnerModule } from '../../../components/spinner/spinner.module';
import { SafeUrlPipeModule } from '../../../shared/pipes/safe-url/safeUrlPipe.module';

import { EnlargeDocumentComponent } from './enlarge-document.component';


@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
    SpinnerModule,
    SafeUrlPipeModule,
  ],
  declarations: [
    EnlargeDocumentComponent,
  ],
  exports: [
    EnlargeDocumentComponent,
  ],
  entryComponents: [
    EnlargeDocumentComponent,
  ]
})
export class EnlargeDocumentModule { }
