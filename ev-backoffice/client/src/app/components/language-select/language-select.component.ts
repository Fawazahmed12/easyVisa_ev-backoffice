import { Component } from '@angular/core';

import { Languages, I18nService } from '../../core/i18n/i18n.service';

@Component({
  selector: 'app-language-select',
  templateUrl: './language-select.component.html',
})
export class LanguageSelectComponent {

  languages = [
    { label: 'English', value: Languages.en },
    { label: 'Español', value: Languages.es },
    { label: 'Tagalog', value: Languages.tl },
    { label: '中文', value: Languages.zh },
  ];

  constructor(
    private i18nService: I18nService,
  ) {
  }

  onLangChange(lang) {
    this.i18nService.changeLang(lang);
  }
}
