import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { RepresentativeTypePipeModule } from '../../shared/pipes/representative-type/representative-type-pipe.module';

import { SelectActiveOrganizationModule } from '../select-active-organization/select-active-organization.module';

import { SelectOrganizationRepresentativeComponent } from './select-organization-representative.component';
import { SelectRepresentativeHeaderModule } from '../select-representative-header/select-representative-header.module';

@NgModule({
  imports: [
    SharedModule,
    SelectActiveOrganizationModule,
    RepresentativeTypePipeModule,
    SelectRepresentativeHeaderModule
  ],
  declarations: [
    SelectOrganizationRepresentativeComponent,
  ],
  exports: [
    SelectOrganizationRepresentativeComponent,
  ],
})
export class SelectOrganizationRepresentativeModule { }
