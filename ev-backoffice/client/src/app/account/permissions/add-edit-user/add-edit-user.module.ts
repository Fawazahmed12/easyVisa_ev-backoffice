import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../../../shared/shared.module';
import { BlockModule } from '../../../shared/components/block/block.module';
import { OrganizationTypeModule } from '../../../shared/pipes/organization-type/organization-type.module';
import { RepresentativeTypePipeModule } from '../../../shared/pipes/representative-type/representative-type-pipe.module';
import { NameFormGroupModule } from '../../../components/name-form-group/name-form-group.module';
import { PhoneFieldModule } from '../../../components/phone-field/phone-field.module';
import { SignUpService } from '../../../auth/services';

import { AddEditUserComponent } from './add-edit-user.component';
import { AddEditUserRoutingModule } from './add-edit-user-routing.module';
import { SpinnerModule } from '../../../components/spinner/spinner.module';


@NgModule({
  imports: [
    SharedModule,
    CommonModule,
    AddEditUserRoutingModule,
    BlockModule,
    NameFormGroupModule,
    PhoneFieldModule,
    OrganizationTypeModule,
    RepresentativeTypePipeModule,
    SpinnerModule,
  ],
  declarations: [
    AddEditUserComponent,
  ],
  exports: [
    AddEditUserComponent,
  ],
  providers: [
    SignUpService
  ]
})
export class AddEditUserModule {
}
