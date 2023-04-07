import { NgModule } from '@angular/core';

import { HideByPositionDirective } from './hide-by-position-directive';


@NgModule({
  declarations: [
    HideByPositionDirective,
  ],
  exports: [
    HideByPositionDirective,
  ]
})
export class HideByPositionDirectiveModule {
}
