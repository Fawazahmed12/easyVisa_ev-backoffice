import { Injectable } from '@angular/core';
import { of } from 'rxjs';

@Injectable()
export class ModalServiceMock {
  openConfirmModal() {
    return of({});
  }
}
