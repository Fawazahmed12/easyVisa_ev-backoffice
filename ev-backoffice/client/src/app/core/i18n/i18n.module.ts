import { NgModule } from '@angular/core';

import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';

import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';

import { I18nService } from './i18n.service';
import { I18N } from './i18n.state';
import { reducer } from './i18n.reducer';
import { I18nEffects } from './i18n.effects';
import { I18nResolverService } from './i18n-resolver.service';
import { RawHttpClient } from '../raw-http-client';

import cacheBusting from '../../../i18n-cache-busting.json';


export function TranslateHttpLoaderFactory(http: RawHttpClient) {
  const suffixWithCacheBusting = !!cacheBusting['en'] ? `.json?v=${cacheBusting['en']}` : `.json`;
  return new TranslateHttpLoader(http, './assets/i18n/', suffixWithCacheBusting);
}

@NgModule({
  imports: [
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: TranslateHttpLoaderFactory,
        deps: [ RawHttpClient ]
      }
    }),
    StoreModule.forFeature(I18N, reducer),
    EffectsModule.forFeature([ I18nEffects ]),
  ],
  exports: [
    TranslateModule,
  ],
  providers: [
    I18nService,
    I18nResolverService,
  ]
})
export class I18nModule {
}
