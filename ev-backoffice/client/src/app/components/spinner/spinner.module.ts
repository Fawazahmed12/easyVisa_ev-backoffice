import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { SpinnerComponent } from './spinner.component';

@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [
    SpinnerComponent,
  ],
  exports: [
    SpinnerComponent,
  ],
})
export class SpinnerModule {
}
