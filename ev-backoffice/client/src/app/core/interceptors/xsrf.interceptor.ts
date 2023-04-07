import {HttpHandler, HttpInterceptor, HttpRequest, HttpXsrfTokenExtractor} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {CSRF} from '../models/csrf.enum';

@Injectable()
export class XsrfInterceptor implements HttpInterceptor {

  /**
   * @param tokenExtractor
   */
  public constructor(private tokenExtractor: HttpXsrfTokenExtractor) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    let requestToForward = req;
    const token = this.tokenExtractor.getToken() as string;
    if (token !== null) {
      requestToForward = req.clone({
        headers: req.headers.set(CSRF.HEADER_NAME, token),
        withCredentials: true
      });
    }
    return next.handle(requestToForward);
  }
}
