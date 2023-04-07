import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { BlockModule } from '../../shared/components/block/block.module';
import { PreviewEmailModule } from '../../components/preview-email/preview-email.module';

import { ApplicantTypePipeModule } from '../pipes/applicantTypePipe.module';

import { FeeItemModule } from './fee-item/fee-item.module';
import { AdditionalFeesComponent } from './additional-fees.component';
import { AdditionalFeesRoutingModule } from './additional-fees-routing.module';
import { GovernmentFeesModalModule } from './modals/government-fees-modal/government-fees-modal.module';
import { EditAmountOwedModule } from '../clients/package-applicants/edit-amount-owed/edit-amount-owed.module';


@NgModule({
  imports: [
    SharedModule,
    BlockModule,
    FeeItemModule,
    EditAmountOwedModule,
    ApplicantTypePipeModule,
    GovernmentFeesModalModule,
    AdditionalFeesRoutingModule,
    PreviewEmailModule,
  ],
  declarations: [
    AdditionalFeesComponent
  ],

})
export class AdditionalFeesModule {
}
