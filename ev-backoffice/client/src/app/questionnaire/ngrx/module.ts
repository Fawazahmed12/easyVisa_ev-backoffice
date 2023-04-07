import { NgModule } from '@angular/core';

import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';

import { effects } from './effects';
import { reducers } from './reducer';
import { QUESTIONNAIRE_NGRX_PROVIDERS } from './services';
import { QUESTIONNAIRE_MODULE_STATE } from './state';

const STORE_IMPORTS = [
  StoreModule.forFeature(QUESTIONNAIRE_MODULE_STATE, reducers),
  EffectsModule.forFeature(effects),
];

@NgModule({
  imports: [
    STORE_IMPORTS,
  ],
  providers: [
    QUESTIONNAIRE_NGRX_PROVIDERS
  ]
})

export class NgrxQuestionnaireModule {
}
