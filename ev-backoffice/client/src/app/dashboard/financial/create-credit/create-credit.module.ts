import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';

import { CreateCreditComponent } from './create-credit.component';


@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [CreateCreditComponent],
  exports: [
    CreateCreditComponent
  ]
})
export class CreateCreditModule {
}
