import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../shared/shared.module';
import { FindLabelPipeModule } from '../../../../../../shared/pipes/find-label/find-label-pipe.module';

import { EducationFormComponent } from './education-form.component';

@NgModule({
  imports: [
    SharedModule,
    FindLabelPipeModule
  ],
  declarations: [
    EducationFormComponent,
  ],
  exports: [
    EducationFormComponent,
  ]
})

export class EducationFormModule {
}
