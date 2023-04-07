import { createFeatureSelector, createSelector } from '@ngrx/store';
import { createEntityAdapter, EntityAdapter, EntityState } from '@ngrx/entity';

import { Languages } from './i18n.service';


export const I18N = 'I18n';

export interface LoadTranslationParams {
  language: Languages;
  path: string;
}

export interface TranslationModule {
  languages: string[];
  path: string;
}

export interface I18nState extends EntityState<TranslationModule> {
  currentLang: Languages;
  defaultLang: Languages;
}


export const adapter: EntityAdapter<TranslationModule> = createEntityAdapter<TranslationModule>({
  selectId: (entity) => entity.path
});

export const { selectAll } = adapter.getSelectors();

export const selectI18nState = createFeatureSelector<I18nState>(I18N);

export const getCurrentLang = createSelector(
  selectI18nState,
  (state: I18nState) => state.currentLang,
);

export const getTranslationModulePaths = createSelector(
  selectI18nState,
  (state: I18nState) => state.ids,
);

export const getTranslationModules = createSelector(
  selectI18nState,
  selectAll,
);
