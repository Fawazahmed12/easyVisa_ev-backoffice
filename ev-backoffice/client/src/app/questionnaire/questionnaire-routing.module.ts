import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { QuestionnaireComponent } from './questionnaire.component';
import { SectionComponent } from './formly/section/section.component';
import { DeactiveQuestionnaireGuardService } from './guards/deactive-questionnaire-guard.service';
import { ActiveQuestionnaireGuardService } from './guards/active-questionnaire-guard.service';
import { ActiveSectionGuardService } from './guards/active-section-guard.service';

export const routes: Routes = [
  {
    path: 'package/:packageId',
    component: QuestionnaireComponent,
    canActivate: [ ActiveSectionGuardService ],
    children: [
      {
        path: 'applicants/:applicantId/sections/:sectionId',
        component: SectionComponent,
        canActivate: [ ActiveQuestionnaireGuardService ],
        canDeactivate: [ DeactiveQuestionnaireGuardService ],
      }
    ],
  }
];

@NgModule({
  imports: [
    RouterModule.forChild(routes),
  ],
  exports: [
    RouterModule,
  ],
})
export class QuestionnaireRoutingModule {
}
