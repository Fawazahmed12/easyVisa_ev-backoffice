import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { ModalHeaderModule } from '../../../components/modal-header/modal-header.module';
import { NoResultsModalComponent } from './no-results-modal.component';


@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
  ],
  declarations: [
    NoResultsModalComponent,
  ],
  exports: [
    NoResultsModalComponent,
  ],
  entryComponents: [
    NoResultsModalComponent,
  ],
})

export class NoResultsModalModule {
}
