import { NgModule } from '@angular/core';

import { IfNoRepresentativeSelectedDirective } from './if-no-representative-selected.directive';


@NgModule({
  declarations: [
    IfNoRepresentativeSelectedDirective,
  ],
  exports: [
    IfNoRepresentativeSelectedDirective,
  ]
})
export class IfNoRepresentativeSelectedDirectiveModule {
}
