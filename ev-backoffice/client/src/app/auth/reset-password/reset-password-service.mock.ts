import { Injectable } from '@angular/core';
import { of } from 'rxjs';

@Injectable()
export class ResetPasswordServiceMock {

  resetPassword(data?) {
    return of(true);
  }
}
