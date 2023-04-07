import { Injectable } from '@angular/core';
import {HttpClient, HttpXsrfTokenExtractor} from '@angular/common/http';

@Injectable()
export class XsrfAppLoadService {

  /**
   * @param http
   */
  public constructor(private http: HttpClient, private tokenExtractor: HttpXsrfTokenExtractor) {
  }

  /**
   * Initialize the CSRF cookie
   */
  public initializeApp(): Promise<any> {
    const xsrfApiUrl = '/public/generate-xsrf';
    return new Promise((resolve) => {
      this.http.get(xsrfApiUrl, {withCredentials: true, observe: 'response'})
        .subscribe(resolve);
    });
  }

  /**
   * Re-Initialize the CSRF cookie ( While ReLogin immideiate after logout, without refeshing our page )
   */
  public loadXSRFIfNotExist(): Promise<any> {
    const token = this.tokenExtractor.getToken() as string;
    if (token !== null) {
      return Promise.resolve({token});
    }
    return this.initializeApp();
  }
}
