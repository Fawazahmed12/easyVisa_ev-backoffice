import { Injectable } from '@angular/core';

import { select, Store } from '@ngrx/store';

import { TranslateService } from '@ngx-translate/core';

import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

import { State } from '../ngrx/state';

import { DefaultLangChange, LangChange, LoadTranslation } from './i18n.actions';
import { getCurrentLang, getTranslationModules, LoadTranslationParams } from './i18n.state';

export enum Languages {
  en = 'en',
  es = 'es',
  tl = 'tl',
  zh = 'zh',
}

@Injectable()
export class I18nService {

  currentLangTranslations$: Observable<Object>;
  currentLang$: Observable<Languages>;
  loadedTranslations$: Observable<any>;

  constructor(
    private store: Store<State>,
    private translateService: TranslateService,
  ) {
    this.currentLang$ = this.store.pipe(select(getCurrentLang));

    this.currentLangTranslations$ = this.currentLang$.pipe(
      switchMap((lang: Languages) =>
        this.translateService.getTranslation(lang)
      )
    );

    this.loadedTranslations$ = this.store.pipe(select(getTranslationModules));

  }

  setDefaultLanguage(lang: Languages) {
    this.store.dispatch(new DefaultLangChange(lang));
  }

  changeLang(lang: Languages) {
    this.store.dispatch(new LangChange(lang));
  }

  loadTranslationModule(data: LoadTranslationParams) {

    this.store.dispatch(new LoadTranslation({ path: data.path, language: data.language }));

    return this.loadedTranslations$;
  }
}
