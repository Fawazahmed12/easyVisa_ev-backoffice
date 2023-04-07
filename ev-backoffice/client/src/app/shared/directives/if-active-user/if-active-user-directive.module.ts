import { NgModule } from '@angular/core';

import { IfActiveUserDirective } from './if-active-user.directive';


@NgModule({
  declarations: [
    IfActiveUserDirective,
  ],
  exports: [
    IfActiveUserDirective,
  ]
})
export class IfActiveUserDirectiveModule {
}
