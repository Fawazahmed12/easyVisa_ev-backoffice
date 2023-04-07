import { NgModule } from '@angular/core';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';

import { effects } from './effects';
import { reducers } from './reducer';
import {  NGRX_PROGRESS_STATUSES_PROVIDERS } from './services';
import {  PROGRESS_STATUSES_MODULE_STATE } from './state';


const STORE_IMPORTS = [
  StoreModule.forFeature(PROGRESS_STATUSES_MODULE_STATE, reducers),
  EffectsModule.forFeature(effects),
];

@NgModule({
  imports: [
    STORE_IMPORTS,
  ],
  providers: [
    NGRX_PROGRESS_STATUSES_PROVIDERS,
  ]
})

export class NgrxProgressStatusModule {
}
