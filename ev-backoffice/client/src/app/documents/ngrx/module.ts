import { NgModule } from '@angular/core';

import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';

import { effects } from './effects';
import { reducers } from './reducer';
import { DOCUMENTS_NGRX_PROVIDERS } from './services';
import { DOCUMENTS_MODULE_STATE } from './state';

const STORE_IMPORTS = [
  StoreModule.forFeature(DOCUMENTS_MODULE_STATE, reducers),
  EffectsModule.forFeature(effects),
];

@NgModule({
  imports: [
    STORE_IMPORTS,
  ],
  providers: [
    DOCUMENTS_NGRX_PROVIDERS
  ]
})

export class NgrxDocumentsModule {
}
