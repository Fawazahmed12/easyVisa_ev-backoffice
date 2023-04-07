import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { RepresentativeTypePipeModule } from '../../shared/pipes/representative-type/representative-type-pipe.module';

import { AuthFormFieldModule } from '../../components/auth-form-field/auth-form-field.module';
import { PhoneFieldModule } from '../../components/phone-field/phone-field.module';
import { AddressModule } from '../../components/address/address.module';
import { RepBasicInfoRoutingModule } from './rep-basic-info-routing.module';
import { RepBasicInfoComponent } from './rep-basic-info.component';

@NgModule({
  imports: [
    SharedModule,
    AuthFormFieldModule,
    AddressModule,
    PhoneFieldModule,
    RepBasicInfoRoutingModule,
    RepresentativeTypePipeModule,
  ],
  declarations: [
    RepBasicInfoComponent,
  ]
})
export class RepBasicInfoModule {
}
