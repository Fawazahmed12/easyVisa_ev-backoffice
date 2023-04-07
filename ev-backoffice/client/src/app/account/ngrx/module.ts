import { NgModule } from '@angular/core';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';

import { effects } from './effects';
import { reducers } from './reducer';
import { ACCOUNT_MODULE_STATE } from './state';
import { NGRX_ACCOUNT_PROVIDERS } from './services';

const STORE_IMPORTS = [
  StoreModule.forFeature(ACCOUNT_MODULE_STATE, reducers),
  EffectsModule.forFeature(effects),
];

@NgModule({
  imports: [
    STORE_IMPORTS,
  ],
  providers: [
    NGRX_ACCOUNT_PROVIDERS,
  ]
})

export class NgrxAccountModule {
}
