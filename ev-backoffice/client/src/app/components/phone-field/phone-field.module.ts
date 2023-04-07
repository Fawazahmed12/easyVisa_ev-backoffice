import { NgModule } from '@angular/core';

import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { SharedModule } from '../../shared/shared.module';

import { PhoneFieldComponent } from './phone-field.component';

@NgModule({
  imports: [
    SharedModule,
    NgbTooltipModule,
  ],
  exports: [
    PhoneFieldComponent,
  ],
  declarations: [
    PhoneFieldComponent,
  ]
})
export class PhoneFieldModule {
}
