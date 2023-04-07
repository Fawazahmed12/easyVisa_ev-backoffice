import { NgModule } from '@angular/core';

import { FormlyModule } from '@ngx-formly/core';

import { FormlyTypesModule } from './types/formly-types.module';
import { RepeatTypeComponent } from './types/repeat-type.component';
import { EvDateComponent } from './types/ev-date.component';

import { FORMLY_WRAPPERS } from './wrappers';
import { FormlyComponentsModule } from './components/formly-components.module';
import { EvAlienNumberComponent } from './types/ev-aliennumber.component';
import { EvUscisNumberComponent } from './types/ev-uscisnumber.component';
import { EvNgDatePickerComponent } from './types/ev-ngdatepicker.component';
import { EvEmailAddressComponent } from './types/ev-emailaddress.component';
import { EvPhoneNumberComponent } from './types/ev-phonenumber.component';
import { EvNumberinputComponent } from './types/ev-numberinput.component';
import { EvSocialSecurityNumberComponent } from './types/ev-socialsecuritynumber.component';
import { EvCurrencyInputComponent } from './types/ev-currencyinput.component';
import { EvAlphaNumericComponent } from './types/ev-alphanumeric.component';
import { EvI94RecordNumberComponent } from './types/ev-i94recordnumber.component';
import { EvTextInputComponent } from './types/ev-textinput.component';
import { EvUscisElisAccountNumberComponent } from './types/ev-usciselisaccountnumber.component';
import { EvConditionalResidenceStatusComponent } from './types/ev-conditionalresidencestatus.component';
import { EvNgSelectComponent } from './types/ev-ngselect.component';
import { EvBmiNumberInputComponent } from './types/ev-bminumberinput.component';

@NgModule({
  imports: [
    FormlyTypesModule,
    FormlyComponentsModule,
    FormlyModule.forRoot({
      extras: { immutable: true },
      wrappers: FORMLY_WRAPPERS,
      types: [
        { name: 'repeat', component: RepeatTypeComponent },
        { name: 'ev-date', component: EvDateComponent, wrappers: [ 'form-field' ] },
        { name: 'ev-aliennumber', component: EvAlienNumberComponent, wrappers: [ 'form-field' ] },
        { name: 'ev-uscisnumber', component: EvUscisNumberComponent, wrappers: [ 'form-field' ] },
        { name: 'ev-ngdatepicker', component: EvNgDatePickerComponent, wrappers: [ 'form-field' ] },
        { name: 'ev-phonenumber', component: EvPhoneNumberComponent, wrappers: [ 'form-field' ] },
        { name: 'ev-emailaddress', component: EvEmailAddressComponent, wrappers: [ 'form-field' ] },
        { name: 'ev-numberinput', component: EvNumberinputComponent, wrappers: [ 'form-field' ] },
        { name: 'ev-socialsecuritynumber', component: EvSocialSecurityNumberComponent, wrappers: [ 'form-field' ] },
        { name: 'ev-currencyinput', component: EvCurrencyInputComponent, wrappers: [ 'form-field' ] },
        { name: 'ev-alphanumberic', component: EvAlphaNumericComponent, wrappers: [ 'form-field' ] },
        { name: 'ev-i94number', component: EvI94RecordNumberComponent, wrappers: [ 'form-field' ] },
        { name: 'ev-textinput', component: EvTextInputComponent, wrappers: [ 'form-field' ] },
        { name: 'ev-usciselisaccountnumber', component: EvUscisElisAccountNumberComponent, wrappers: [ 'form-field' ] },
        { name: 'ev-conditionalresidencestatus', component: EvConditionalResidenceStatusComponent, wrappers: [ 'form-field' ] },
        { name: 'ev-ngselect', component: EvNgSelectComponent, wrappers: [ 'form-field' ] },
        { name: 'ev-bminumberinput', component: EvBmiNumberInputComponent, wrappers: [ 'form-field' ] },
      ]
    }),
  ],
})
export class CoreFormlyModule {
}
