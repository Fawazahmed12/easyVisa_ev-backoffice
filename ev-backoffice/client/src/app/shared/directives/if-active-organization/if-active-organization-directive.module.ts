import { NgModule } from '@angular/core';

import { IfActiveOrganizationDirective } from './if-active-organization.directive';

@NgModule({
  declarations: [
    IfActiveOrganizationDirective,
  ],
  exports: [
    IfActiveOrganizationDirective,
  ]
})
export class IfActiveOrganizationDirectiveModule {
}
