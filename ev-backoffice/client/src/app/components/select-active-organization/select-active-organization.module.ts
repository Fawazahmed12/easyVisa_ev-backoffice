import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { SelectActiveOrganizationComponent } from './select-active-organization.component';

@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [
    SelectActiveOrganizationComponent,
  ],
  exports: [
    SelectActiveOrganizationComponent,
  ],
})
export class SelectActiveOrganizationModule { }
