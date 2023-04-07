import { of, throwError } from 'rxjs';
import { switchMap } from 'rxjs/operators';

import { User } from '../../models/user.model';
import { RequestState, ResponseStatus } from './request-handler';

export function throwIfRequestFailError() {
  return (observable) => observable.pipe(
    switchMap((response: RequestState<User>) => {
      if (response.status === ResponseStatus.fail) {
        return throwError(response.data);
      }
      return of(response.data);
    }),
  );
}
