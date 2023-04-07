import { HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { I18nService, Languages } from '../i18n/i18n.service';
import { Observable } from 'rxjs';
import { switchMap, take } from 'rxjs/operators';

@Injectable()
export class LanguageInterceptor implements HttpInterceptor {

  public constructor(private i18nService: I18nService) {
  }


  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const currentLang$: Observable<Languages> = this.i18nService.currentLang$;
    return currentLang$.pipe(
      take(1),
      switchMap((currentLang) => {
        if (currentLang) {
          // Clone the request to add the new header
          const clonedRequest = req.clone({ headers: req.headers.set('Accept-Language', currentLang) });
          return next.handle(clonedRequest);
        }
        return next.handle(req);
      })
    );
  }
}
