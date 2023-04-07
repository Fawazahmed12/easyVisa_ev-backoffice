import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../shared/shared.module';

import { PracticeFormComponent } from './practice-form.component';

@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [
    PracticeFormComponent,
  ],
  exports: [
    PracticeFormComponent,
  ]
})

export class PracticeFormModule {
}
