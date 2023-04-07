import { adapter, I18nState } from './i18n.state';
import { I18nActionsUnion, I18nActionTypes } from './i18n.actions';

export const initialState: I18nState = adapter.getInitialState({
  currentLang: null,
  defaultLang: null,
});

export function reducer(state = initialState, action: I18nActionsUnion) {
  switch (action.type) {

    case I18nActionTypes.DefaultLangChange: {
      return {
        ...state,
        defaultLang: action.payload,
      };
    }

    case I18nActionTypes.LangChange: {
      return {
        ...state,
        currentLang: action.payload,
      };
    }

    case I18nActionTypes.LoadTranslationSuccess: {

      const { path, language } = action.payload;

      const previousEntity = state.entities[ path ];

      const prevLanguages = (previousEntity && previousEntity.languages) || [];

      const loadedTranslationPath = { path, languages: [ ...prevLanguages, ...language ] };

      return adapter.upsertOne(loadedTranslationPath, state);
    }

    default: {
      return state;
    }
  }
}
