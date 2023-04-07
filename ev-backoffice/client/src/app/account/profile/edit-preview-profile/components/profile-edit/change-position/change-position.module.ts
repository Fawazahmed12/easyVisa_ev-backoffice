import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../shared/shared.module';

import { ChangePositionComponent } from './change-position.component';

@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [
    ChangePositionComponent,
  ],
  exports: [
    ChangePositionComponent,
  ]
})

export class ChangePositionModule {
}
