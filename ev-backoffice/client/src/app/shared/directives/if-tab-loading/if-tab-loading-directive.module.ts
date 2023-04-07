import { NgModule } from '@angular/core';

import { IfTabLoadingDirective } from './if-tab-loading.directive';



@NgModule({
  declarations: [
    IfTabLoadingDirective,
  ],
  exports: [
    IfTabLoadingDirective,
  ]
})
export class IfTabLoadingDirectiveModule {
}
