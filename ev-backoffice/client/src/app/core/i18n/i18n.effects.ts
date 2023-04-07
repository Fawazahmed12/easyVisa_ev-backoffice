import { Injectable } from '@angular/core';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';

import { TranslateService } from '@ngx-translate/core';

import { from, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, tap, withLatestFrom } from 'rxjs/operators';

import {
  DefaultLangChange,
  LangChange,
  I18nActionTypes,
  LoadTranslation,
  LoadTranslationSuccess, LoadTranslationFailed
} from './i18n.actions';
import { I18nService, Languages } from './i18n.service';
import { TranslationModule, LoadTranslationParams } from './i18n.state';
import { RawHttpClient } from '../raw-http-client';

import cacheBusting from '../../../i18n-cache-busting.json';

@Injectable()
export class I18nEffects {

  @Effect({ dispatch: false })
  changeLang$: Observable<Action> = this.actions$.pipe(
    ofType(I18nActionTypes.LangChange),
    tap(({ payload }: LangChange) => this.translateService.use(payload)),
  );

  @Effect({ dispatch: false })
  setDefaultLang$: Observable<Action> = this.actions$.pipe(
    ofType(I18nActionTypes.DefaultLangChange),
    tap(({ payload }: DefaultLangChange) => this.translateService.setDefaultLang(payload)),
  );

  @Effect()
  initialSetDefaultLang: Observable<Action> = of(new DefaultLangChange(Languages.en));

  @Effect()
  initialSetLang: Observable<Action> = of(new LangChange(Languages.en));

  @Effect()
  loadTranslationOnLangChange$: Observable<Action> = this.actions$.pipe(
    ofType(I18nActionTypes.LangChange),
    withLatestFrom(this.i18nService.loadedTranslations$),
    switchMap(([ { payload: language }, loadedTranslationModules ]: [ LangChange, TranslationModule[] ]) => {

      const translationsToLoad = this.findTranslationModulePathsToLoad(loadedTranslationModules, language);

      return from(translationsToLoad);
    }),
    map((data: LoadTranslationParams) => new LoadTranslation(data)),
  );

  @Effect()
  LoadTranslation$: Observable<Action> = this.actions$.pipe(
    ofType(I18nActionTypes.LoadTranslation),
    mergeMap(({ payload }: LoadTranslation) => {
      const { language, path } = payload;

      const fullPath = !!cacheBusting[`${path}/${language}`]
        ? `/assets/i18n/${path}/${language}.json?v=${cacheBusting[`${path}/${language}`]}`
        : `/assets/i18n/${path}/${language}.json`;
      return this.rawHttpClient.get(fullPath)
      .pipe(
        map((translations) =>
          new LoadTranslationSuccess({ language, translations, path })
        ),
        catchError((error) => of(new LoadTranslationFailed(error))),
      );
    }),
  );

  @Effect({ dispatch: false })
  LoadTranslationSuccess$: Observable<LoadTranslationParams & { translations: any }> = this.actions$.pipe(
    ofType(I18nActionTypes.LoadTranslationSuccess),
    map(({ payload }: LoadTranslationSuccess) => payload),
    tap(({ language, translations }) => {
      this.translateService.setTranslation(language, translations, true);
    })
  );

  constructor(
    private actions$: Actions,
    private translateService: TranslateService,
    private i18nService: I18nService,
    private rawHttpClient: RawHttpClient,
  ) {
  }

  private findTranslationModulePathsToLoad(modules: TranslationModule[], language: string) {
    return modules
    .filter((loadedTranslation: TranslationModule) =>
      !loadedTranslation.languages.find((lang) => lang === language)
    )
    .map(({ path }: TranslationModule) => ({ language, path }));
  }
}
