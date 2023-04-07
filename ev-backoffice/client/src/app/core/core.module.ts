import {APP_INITIALIZER, NgModule} from '@angular/core';
import {HttpClientModule, HttpClientXsrfModule} from '@angular/common/http';

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { StoreModule } from '@ngrx/store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { EffectsModule } from '@ngrx/effects';

import { environment } from '../../environments/environment';

import { metaReducers, reducers } from './ngrx/reducer';
import { effects } from './ngrx/effects';
import { NGRX_PROVIDERS } from './ngrx/services';

import { GUARD_PROVIDERS } from './guards';
import { PROVIDERS } from './services';
import { I18nModule } from './i18n/i18n.module';
import { RawHttpClientModule } from './raw-http-client';
import { INTERCEPTORS } from './interceptors';
import { RESOLVERS } from './resolvers';
import {
  RequestTransferSentModalModule
} from '../task-queue/clients/packages/modals/request-transfer-sent-modal/request-transfer-sent-modal.module';
import { MODALS } from './modals';
import { ArticlesService } from '../dashboard/articles/articles.service';
import { QuillModule } from 'ngx-quill';
import {XsrfAppLoadService} from './services/xsrf-app-load.service';
import {CSRF} from './models/csrf.enum';

const STORE_IMPORTS = [
  StoreModule.forRoot(reducers, {metaReducers}),
  EffectsModule.forRoot(effects),
];

if (!environment.production) {
  STORE_IMPORTS.push(
    StoreDevtoolsModule.instrument({
      maxAge: 25,
      logOnly: environment.production,
    })
  );
}


/**
 * App initializer
 *
 * @param appLoadService
 */
export function initApp(appLoadService: XsrfAppLoadService) {
  return () => appLoadService.initializeApp();
}

@NgModule({
  imports: [

    STORE_IMPORTS,

    I18nModule,

    NgbModule,
    RawHttpClientModule,
    HttpClientModule,
    QuillModule.forRoot(),
    HttpClientXsrfModule.withOptions({  // Adds xsrf support
      cookieName: CSRF.COOKIE_NAME,
      headerName: CSRF.HEADER_NAME
    }),
    RequestTransferSentModalModule,
    MODALS,
  ],
  providers: [
    GUARD_PROVIDERS,
    PROVIDERS,
    NGRX_PROVIDERS,
    INTERCEPTORS,
    RESOLVERS,
    ArticlesService,
    {
      provide: APP_INITIALIZER,
      useFactory: initApp,
      deps: [XsrfAppLoadService],
      multi: true
    },
  ],
})
export class CoreModule {
}
