import { Action } from '@ngrx/store';

import { I18N, LoadTranslationParams } from './i18n.state';
import { Languages } from './i18n.service';

export const I18nActionTypes = {
  LangChange: `[${I18N}] Lang Change`,
  DefaultLangChange: `[${I18N}] Default Lang Change`,
  LoadTranslation: `[${I18N}] Load Translation`,
  LoadTranslationSuccess: `[${I18N}] Load Translation Success`,
  LoadTranslationFailed: `[${I18N}] Load Translation Failed`,
};

export class LangChange implements Action {
  readonly type = I18nActionTypes.LangChange;

  constructor(public payload: Languages) {
  }
}

export class DefaultLangChange implements Action {
  readonly type = I18nActionTypes.DefaultLangChange;

  constructor(public payload: Languages) {
  }
}

export class LoadTranslation implements Action {
  readonly type = I18nActionTypes.LoadTranslation;

  constructor(public payload: LoadTranslationParams) {
  }
}

export class LoadTranslationSuccess implements Action {
  readonly type = I18nActionTypes.LoadTranslationSuccess;

  constructor(public payload: LoadTranslationParams & { translations: any }) {
  }
}

export class LoadTranslationFailed implements Action {
  readonly type = I18nActionTypes.LoadTranslationFailed;

  constructor(public payload: any) {
  }
}

export type I18nActionsUnion =
  | LangChange
  | DefaultLangChange
  | LoadTranslation
  | LoadTranslationFailed
  | LoadTranslationSuccess;

