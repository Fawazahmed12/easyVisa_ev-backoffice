import { HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';
import { switchMap, take } from 'rxjs/operators';

import { AuthService } from '../services';

@Injectable()
export class HeaderAuthorizationInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler) {

    const token$: Observable<string> = this.authService.currentUserToken$;

    return token$.pipe(
      take(1),
      switchMap((token) => {
        if (token) {
          req = req.clone({ headers: req.headers.set('Authorization', `Bearer ${token}`) });
        }
        return next.handle(req);
      })
    );
  }
}
