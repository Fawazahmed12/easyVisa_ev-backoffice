import { Observable, ReplaySubject } from 'rxjs';

import { Languages } from '../i18n/i18n.service';

export class I18nServiceMock {

  currentLangSubject$ = new ReplaySubject<Languages>(1);

  get currentLang$(): Observable<Languages> {
    this.currentLangSubject$.next(Languages.en);

    return this.currentLangSubject$.asObservable();
  }
}
