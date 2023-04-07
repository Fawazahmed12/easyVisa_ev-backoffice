import { NgModule } from '@angular/core';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';

import { effects } from './effects';
import { reducers } from './reducer';
import { NGRX_DASHBOARD_PROVIDERS } from './services';
import { DASHBOARD_MODULE_STATE } from './state';


const STORE_IMPORTS = [
  StoreModule.forFeature(DASHBOARD_MODULE_STATE, reducers),
  EffectsModule.forFeature(effects),
];

@NgModule({
  imports: [
    STORE_IMPORTS,
  ],
  providers: [
    NGRX_DASHBOARD_PROVIDERS,
  ]
})

export class NgrxDashboardModule {
}
