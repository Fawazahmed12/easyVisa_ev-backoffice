import { NgModule } from '@angular/core';

import { IfCurrentRepresentativeNotAdminMeDirective } from './if-current-representative-not-admin-me.directive';


@NgModule({
  declarations: [
    IfCurrentRepresentativeNotAdminMeDirective,
  ],
  exports: [
    IfCurrentRepresentativeNotAdminMeDirective,
  ]
})
export class IfCurrentRepresentativeNotAdminMeDirectiveModule {
}
