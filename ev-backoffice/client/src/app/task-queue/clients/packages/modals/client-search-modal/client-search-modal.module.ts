import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../shared/shared.module';
import { ModalHeaderModule } from '../../../../../components/modal-header/modal-header.module';

import { ClientSearchModalComponent } from './client-search-modal.component';
import { IndividualClientSearchModule } from './individual-client-search/individual-client-search.module';
import { MultipleClientSearchModule } from './multiple-client-search/multiple-client-search.module';

@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
    IndividualClientSearchModule,
    MultipleClientSearchModule,
  ],
  declarations: [
    ClientSearchModalComponent,
  ],
  exports: [
    ClientSearchModalComponent,
  ],
  entryComponents: [
    ClientSearchModalComponent,
  ],
})

export class ClientSearchModalModule {
}
