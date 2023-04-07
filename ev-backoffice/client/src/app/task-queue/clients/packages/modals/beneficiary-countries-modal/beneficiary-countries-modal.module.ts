import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../shared/shared.module';
import { ModalHeaderModule } from '../../../../../components/modal-header/modal-header.module';

import { BeneficiaryCountriesModalComponent } from './beneficiary-countries-modal.component';

@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
  ],
  declarations: [
    BeneficiaryCountriesModalComponent,
  ],
  exports: [
    BeneficiaryCountriesModalComponent,
  ],
  entryComponents: [
    BeneficiaryCountriesModalComponent,
  ],
})

export class BeneficiaryCountriesModalModule {
}
