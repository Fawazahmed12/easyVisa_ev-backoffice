import { Injectable, NgZone } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { switchMap, take } from 'rxjs/operators';
import { delayWhen } from 'rxjs/operators';

import { I18nService } from './i18n.service';

@Injectable()
export class I18nResolverService implements Resolve<any> {

  constructor(
    private i18nService: I18nService,
    private ngZone: NgZone,
  ) {

  }
  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ) {
    const translationUrl = route.data.translationUrl;

    return this.i18nService.currentLang$.pipe(
      switchMap((lang) =>
        this.i18nService.loadTranslationModule({path: translationUrl, language: lang})
      ),
      // To pause route loading until all changes is applied (translation were set)
      // Used to avoid blinking with translation constants
      delayWhen(() => this.ngZone.onMicrotaskEmpty),
      take(1),
    );
  }
}
