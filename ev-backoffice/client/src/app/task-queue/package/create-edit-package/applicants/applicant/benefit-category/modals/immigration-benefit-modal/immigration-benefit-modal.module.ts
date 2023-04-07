import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../../../shared/shared.module';
import { ModalHeaderModule } from '../../../../../../../../components/modal-header/modal-header.module';

import { ImmigrationBenefitModalComponent } from './immigration-benefit-modal.component';

@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
  ],
  declarations: [
    ImmigrationBenefitModalComponent,
  ],
  exports: [
    ImmigrationBenefitModalComponent,
  ],
  entryComponents: [
    ImmigrationBenefitModalComponent,
  ],
})

export class ImmigrationBenefitModalModule {
}
