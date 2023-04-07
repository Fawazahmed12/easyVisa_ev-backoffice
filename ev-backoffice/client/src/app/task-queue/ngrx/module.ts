import { NgModule } from '@angular/core';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';

import { effects } from './effects';
import { reducers } from './reducer';
import { TASK_QUEUE_MODULE_STATE } from './state';
import { NGRX_TASK_QUEUE_PROVIDERS } from './services';

const STORE_IMPORTS = [
  StoreModule.forFeature(TASK_QUEUE_MODULE_STATE, reducers),
  EffectsModule.forFeature(effects),
];

@NgModule({
  imports: [
    STORE_IMPORTS,
  ],
  providers: [
    NGRX_TASK_QUEUE_PROVIDERS,
  ]
})

export class NgrxTaskQueueModule {
}
