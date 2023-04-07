import { HTTP_INTERCEPTORS } from '@angular/common/http';

import { BaseUrlInterceptor } from './base-url.interceptor';
import { HeaderAuthorizationInterceptor } from './header-authorization.interceptor';
import { CacheControlInterceptor } from './cache-control.interceptor';
import { UnauthorizedInterceptor } from './unauthorized.interceptor';
import { UnpaidInterceptor } from './unpaid.interceptor';
import { EncodeHttpParamsInterceptor } from './encode-http-params.interceptor';
import { XsrfInterceptor } from './xsrf.interceptor';
import { LanguageInterceptor } from './language.interceptor';
import { CurrentDateInterceptor } from './current-date.interceptor';

export const INTERCEPTORS = [
  { provide: HTTP_INTERCEPTORS, useClass: UnauthorizedInterceptor, multi: true },
  { provide: HTTP_INTERCEPTORS, useClass: BaseUrlInterceptor, multi: true },
  { provide: HTTP_INTERCEPTORS, useClass: HeaderAuthorizationInterceptor, multi: true },
  { provide: HTTP_INTERCEPTORS, useClass: CacheControlInterceptor, multi: true },
  { provide: HTTP_INTERCEPTORS, useClass: UnpaidInterceptor, multi: true },
  { provide: HTTP_INTERCEPTORS, useClass: EncodeHttpParamsInterceptor, multi: true},
  { provide: HTTP_INTERCEPTORS, useClass: XsrfInterceptor, multi: true},
  { provide: HTTP_INTERCEPTORS, useClass: LanguageInterceptor, multi: true},
  { provide: HTTP_INTERCEPTORS, useClass: CurrentDateInterceptor, multi: true},
];
