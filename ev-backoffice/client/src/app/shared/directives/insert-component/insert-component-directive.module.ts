import { NgModule } from '@angular/core';

import { InsertComponentDirective } from './insert-component.directive';


@NgModule({
  declarations: [
    InsertComponentDirective,
  ],
  exports: [
    InsertComponentDirective,
  ]
})
export class InsertComponentDirectiveModule {
}
