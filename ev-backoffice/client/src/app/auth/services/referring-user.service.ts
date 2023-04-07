import { Injectable } from '@angular/core';

import { select, Store } from '@ngrx/store';

import { Observable, of } from 'rxjs';
import { filter, map, share, switchMap } from 'rxjs/operators';

import { RequestState } from '../../core/ngrx/utils';

import { getReferringUsername, getReferringUserRequestState, State } from '../ngrx/state';
import { GetReferringUser } from '../ngrx/attorney-sign-up-info/attorney-sign-up-info.actions';

import { ReferringUserModel } from '../models/referring-user.model';

@Injectable()
export class ReferringUserService {
  checkTokenRequest$: Observable<RequestState<ReferringUserModel>>;

  referringUsername$: Observable<string>;

  constructor(
    private store: Store<State>,
  ) {
    this.checkTokenRequest$ = this.store.pipe(select(getReferringUserRequestState));

    this.referringUsername$ = this.store.pipe(select(getReferringUsername));
  }

  getReferringUser(token) {
    return this.referringUsername$.pipe(
      switchMap((name) => {
        if (name === null) {
          this.store.dispatch(new GetReferringUser(token));
          return this.checkTokenRequest$.pipe(
            filter(response => !response.loading),
            map((response: RequestState<ReferringUserModel>) => response.data),
            share(),
          );
        }
        return of(name);
      })
    );
  }
}
