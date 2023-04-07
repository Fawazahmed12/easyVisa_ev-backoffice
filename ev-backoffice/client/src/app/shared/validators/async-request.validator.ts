import { AbstractControl } from '@angular/forms';

import { debounceTime, switchMap, take, catchError, map } from 'rxjs/operators';
import { ReplaySubject, of, Observable } from 'rxjs';


export class AsyncRequestValidator {
  static createValidator(requestFunction: (value: any) => Observable<null | any>, initValue?, debounce = 500) {
    const requestSubject$ = new ReplaySubject<string>(1);

    const request$ = requestSubject$.pipe(
      debounceTime(debounce),
      switchMap((value) =>
        requestFunction(value).pipe(
          map((res: { valid: boolean; message?: string }) =>
            ((res.valid) ? null : {reqValidator: true, invalidMessage: res.message})),
          catchError((error) => {
            console.error(error);
            return of({reqValidator: true});
          }),
        )
      ),
    );

    return (control: AbstractControl) => {
      if (!initValue || control.value !== initValue) {
        requestSubject$.next(control.value);
        return request$.pipe(
          take(1),
        );
      } else {
        return of(null).pipe(
          debounceTime(debounce),
        );
      }

    };
  }
}
