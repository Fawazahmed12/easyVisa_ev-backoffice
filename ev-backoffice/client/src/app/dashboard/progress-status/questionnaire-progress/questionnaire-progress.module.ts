import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';

import { ProgressUnitModule } from '../progress-unit/progress-unit.module';

import { QuestionnaireProgressComponent } from './questionnaire-progress.component';


@NgModule({
  imports: [
    SharedModule,
    ProgressUnitModule,
  ],
  declarations: [
    QuestionnaireProgressComponent,
  ],
  exports: [
    QuestionnaireProgressComponent,
  ],
})
export class QuestionnaireProgressModule {
}
