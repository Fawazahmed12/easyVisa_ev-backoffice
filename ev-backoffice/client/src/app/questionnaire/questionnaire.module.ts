import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';

import { GUARD_PROVIDERS } from './guards';
import { RESOLVERS } from './resolvers';
import { PROVIDERS } from './services';
import { QuestionnaireRoutingModule } from './questionnaire-routing.module';
import { QuestionnaireComponent } from './questionnaire.component';

import { SharedFormlyModule } from './formly/shared-formly.module';
import { CoreFormlyModule } from './formly/core-formly.module';
import { NgrxQuestionnaireModule } from './ngrx/module';
import { SpinnerModule } from '../components/spinner/spinner.module';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    CoreFormlyModule,
    SharedFormlyModule,
    NgrxQuestionnaireModule,
    QuestionnaireRoutingModule,
    SpinnerModule
  ],
  providers: [
    GUARD_PROVIDERS,
    RESOLVERS,
    PROVIDERS
  ],
  declarations: [
    QuestionnaireComponent
  ]
})
export class QuestionnaireModule {
}
