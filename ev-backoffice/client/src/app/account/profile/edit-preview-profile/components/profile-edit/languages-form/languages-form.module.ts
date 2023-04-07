import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../shared/shared.module';

import { LanguagesFormComponent } from './languages-form.component';
import { FindLabelPipeModule } from '../../../../../../shared/pipes/find-label/find-label-pipe.module';


@NgModule({
  imports: [
    SharedModule,
    FindLabelPipeModule
  ],
  declarations: [
    LanguagesFormComponent,
  ],
  exports: [
    LanguagesFormComponent,
  ]
})

export class LanguagesFormModule {
}
