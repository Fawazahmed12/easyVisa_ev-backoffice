import { NgModule } from '@angular/core';

import { ModalHeaderModule } from '../../../../components/modal-header/modal-header.module';
import { SharedModule } from '../../../../shared/shared.module';

import { PermissionsLevelModalComponent } from './permissions-level-modal.component';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { OrganizationTypeModule } from '../../../../shared/pipes/organization-type/organization-type.module';


@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
    NgbTooltipModule,
    OrganizationTypeModule
  ],
  declarations: [
    PermissionsLevelModalComponent,
  ],
  exports: [
    PermissionsLevelModalComponent,
  ],
  entryComponents: [
    PermissionsLevelModalComponent,
  ],
})

export class PermissionsLevelModalModule {
}
