import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { ModalHeaderModule } from '../../../components/modal-header/modal-header.module';
import { OrganizationTypeModule } from '../../../shared/pipes/organization-type/organization-type.module';

import { MandatoryAdminPositionComponent } from './mandatory-admin-position.component';


@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
    OrganizationTypeModule
  ],
  declarations: [
    MandatoryAdminPositionComponent,
  ],
  exports: [
    MandatoryAdminPositionComponent,
  ],
  entryComponents: [
    MandatoryAdminPositionComponent,
  ],
})

export class MandatoryAdminPositionModule {
}
