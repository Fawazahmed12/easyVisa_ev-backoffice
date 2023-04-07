import { NgModule } from '@angular/core';

import { OrganizationTypePipe } from './organization-type.pipe';

@NgModule({
  declarations: [
    OrganizationTypePipe,
  ],
  exports: [
    OrganizationTypePipe
  ]
})
export class OrganizationTypeModule { }
