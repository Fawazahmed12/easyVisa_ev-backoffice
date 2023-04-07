import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { NgbDatepickerModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormlyModule } from '@ngx-formly/core';

import { MaskModule } from '../../../shared/modules/mask.module';

import { RepeatTypeComponent } from './repeat-type.component';
import { EvDateComponent } from './ev-date.component';
import { EvAlienNumberComponent } from './ev-aliennumber.component';
import { EvUscisNumberComponent } from './ev-uscisnumber.component';
import { EvNgDatePickerComponent } from './ev-ngdatepicker.component';
import { BaseFieldTypeComponent } from './base-fieldtype.component';
import { EvPhoneNumberComponent } from './ev-phonenumber.component';
import { EvEmailAddressComponent } from './ev-emailaddress.component';
import { EvNumberinputComponent } from './ev-numberinput.component';
import { EvSocialSecurityNumberComponent } from './ev-socialsecuritynumber.component';
import { EvCurrencyInputComponent } from './ev-currencyinput.component';
import { EvAlphaNumericComponent } from './ev-alphanumeric.component';
import { EvI94RecordNumberComponent } from './ev-i94recordnumber.component';
import { EvTextInputComponent } from './ev-textinput.component';
import { EvUscisElisAccountNumberComponent } from './ev-usciselisaccountnumber.component';
import { EvConditionalResidenceStatusComponent } from './ev-conditionalresidencestatus.component';
import { EvNgSelectComponent } from './ev-ngselect.component';
import { EvBmiNumberInputComponent } from './ev-bminumberinput.component';


@NgModule({
  imports: [
    CommonModule,
    FormlyModule,
    FormsModule,
    NgbDatepickerModule,
    NgbTooltipModule,
    MaskModule,
    NgSelectModule
  ],
  declarations: [
    BaseFieldTypeComponent,
    RepeatTypeComponent,
    EvDateComponent,
    EvAlienNumberComponent,
    EvUscisNumberComponent,
    EvNgDatePickerComponent,
    EvPhoneNumberComponent,
    EvEmailAddressComponent,
    EvNumberinputComponent,
    EvSocialSecurityNumberComponent,
    EvCurrencyInputComponent,
    EvAlphaNumericComponent,
    EvI94RecordNumberComponent,
    EvTextInputComponent,
    EvUscisElisAccountNumberComponent,
    EvConditionalResidenceStatusComponent,
    EvNgSelectComponent,
    EvBmiNumberInputComponent
  ],
  exports: [
    BaseFieldTypeComponent,
    RepeatTypeComponent,
    EvDateComponent,
    EvAlienNumberComponent,
    EvUscisNumberComponent,
    EvNgDatePickerComponent,
    EvPhoneNumberComponent,
    EvEmailAddressComponent,
    EvNumberinputComponent,
    EvSocialSecurityNumberComponent,
    EvCurrencyInputComponent,
    EvAlphaNumericComponent,
    EvI94RecordNumberComponent,
    EvTextInputComponent,
    EvUscisElisAccountNumberComponent,
    EvConditionalResidenceStatusComponent,
    EvNgSelectComponent,
    EvBmiNumberInputComponent
  ]
})
export class
FormlyTypesModule {
}
