import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../../../shared/shared.module';
import { ModalHeaderModule } from '../../../../../../../../components/modal-header/modal-header.module';

import { PetitionerBenefitModalComponent } from './petitioner-benefit-modal.component';


@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
  ],
  declarations: [
    PetitionerBenefitModalComponent,
  ],
  exports: [
    PetitionerBenefitModalComponent,
  ],
  entryComponents: [
    PetitionerBenefitModalComponent,
  ],
})

export class PetitionerBenefitModalModule {
}
