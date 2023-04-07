import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { FindLabelPipeModule } from '../../../shared/pipes/find-label/find-label-pipe.module';

import { CreateEditPackageRoutingModule } from './create-edit-package-routing.module';
import { CreateEditPackageComponent } from './create-edit-package.component';
import { ApplicantsModule } from './applicants/applicants.module';
import { AssignToModule } from './assign-to/assign-to.module';
import { DispositionPackageModule } from './disposition-package/disposition-package.module';
import { BillingInfoModule } from './billing-info/billing-info.module';
import { SelectPackageTypeModule } from './select-package-type/select-package-type.module';
import { ClosedPackageSelectedModule } from './closed-package-selected/closed-package-selected.module';

@NgModule({
  imports: [
    SharedModule,
    CreateEditPackageRoutingModule,
    ApplicantsModule,
    AssignToModule,
    BillingInfoModule,
    DispositionPackageModule,
    SelectPackageTypeModule,
    ClosedPackageSelectedModule,
    FindLabelPipeModule,
  ],
  declarations: [
    CreateEditPackageComponent,
  ],
})
export class CreateEditPackageModule {
}
