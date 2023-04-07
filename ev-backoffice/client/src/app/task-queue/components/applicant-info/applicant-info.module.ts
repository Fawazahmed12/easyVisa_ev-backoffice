import { NgModule } from '@angular/core';

import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { SharedModule } from '../../../shared/shared.module';

import { AddressModule } from '../../../components/address/address.module';
import { ApplicantTypePipeModule } from '../../pipes/applicantTypePipe.module';
import { DatepickerGroupModule } from '../../../components/datepicker-group/datepicker-group.module';
import { PhoneFieldModule } from '../../../components/phone-field/phone-field.module';
import { HorizontalFormFieldModule } from '../../package/create-edit-package/components/horizontal-form-field/horizontal-form-field.module';
import { ApplicantInfoComponent } from './applicant-info.component';
import { NameFormGroupModule } from '../../../components/name-form-group/name-form-group.module';
import { FindLabelPipeModule } from '../../../shared/pipes/find-label/find-label-pipe.module';

@NgModule({
  imports: [
    SharedModule,
    AddressModule,
    ApplicantTypePipeModule,
    DatepickerGroupModule,
    PhoneFieldModule,
    NgbTooltipModule,
    HorizontalFormFieldModule,
    NameFormGroupModule,
    FindLabelPipeModule,
  ],
  declarations: [
    ApplicantInfoComponent,
  ],
  exports: [
    ApplicantInfoComponent,
  ]
})
export class ApplicantInfoModule {
}
