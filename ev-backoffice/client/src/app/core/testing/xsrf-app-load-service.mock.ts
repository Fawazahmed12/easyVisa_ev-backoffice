import {Injectable} from '@angular/core';

@Injectable()
export class XsrfAppLoadServiceMock {

  public initializeApp(): Promise<any> {
    return Promise.resolve({token: 'Sample Test Token'});
  }

  public loadXSRFIfNotExist(): Promise<any> {
    return Promise.resolve({token: 'Sample Test Token'});
  }
}
