import { HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable()
export class CacheControlInterceptor implements HttpInterceptor {

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const httpRequest = req.clone({
       headers: req.headers.append('Cache-Control', 'no-cache')
    });

    return next.handle(httpRequest);
  }
}
