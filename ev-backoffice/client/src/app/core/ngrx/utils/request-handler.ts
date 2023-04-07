import { Action } from '@ngrx/store';
import { ofType } from '@ngrx/effects';

import { of } from 'rxjs';
import { catchError, map, mergeMap, concatMap, switchMap } from 'rxjs/operators';

export interface RequestState<T> {
  loading: boolean;
  loaded: boolean;
  status: string;
  data: T;
}

export enum ResponseStatus {
  success = 'success',
  fail = 'fail',
}

export const requestInitialState: RequestState<any> = {
  loading: false,
  loaded: false,
  status: '',
  data: null
};

export class RequestAction<T> implements Action {
  type: string;

  constructor(public payload?: T | any) {
  }
}

export class RequestSuccessAction<T> implements Action {
  type: string;

  constructor(public payload?: T | any) {
  }
}

export class RequestFailAction<T> implements Action {
  type: string;

  constructor(public payload?: T | any) {
  }
}

export function createRequestHandler(name, concurrency = 'merge') {
  const ActionTypes = {
    REQUEST: `[${name}] Request`,
    REQUEST_SUCCESS: `[${name}] Request Success`,
    REQUEST_FAIL: `[${name}] Request Fail`,
  };

  const requestAction = (payload?) => {
    const action = new RequestAction(payload);
    action.type = ActionTypes.REQUEST;
    return action;
  };

  const successAction = (payload?) => {
    const action = new RequestSuccessAction(payload);
    action.type = ActionTypes.REQUEST_SUCCESS;
    return action;
  };

  const failAction = (payload?) => {
    const action = new RequestFailAction(payload);
    action.type = ActionTypes.REQUEST_FAIL;
    return action;
  };

  // e.g. mergeMap, switchMap, concatMap, etc
  // const concurrencyOperator = `${concurrency}Map`;
  const concurrencyOperator = (callback) => {
    switch (concurrency) {
      case 'merge': {
        return mergeMap(callback);
      }
      case 'concat': {
        return concatMap(callback);
      }
      case 'switch': {
        return switchMap(callback);
      }
    }
  };

  const effect = (actions$, requestFn) =>
    actions$.pipe(
      ofType(
        ActionTypes.REQUEST
      ),
      concurrencyOperator(action => requestFn(action.payload).pipe(
          map(response =>
            successAction(response)
          ),
          catchError(error => of(
            failAction(error)
          ))
        ))
    );

  const reducer = (state: RequestState<any> = requestInitialState, action) => {

    switch (action.type) {
      case ActionTypes.REQUEST:
        return {
          ...state,
          loading: true,
          loaded: false,
          status: '',
          data: null
        };

      case ActionTypes.REQUEST_SUCCESS:
        return {
          ...state,
          loading: false,
          loaded: true,
          status: ResponseStatus.success,
          data: action.payload
        };

      case ActionTypes.REQUEST_FAIL:
        return {
          ...state,
          loading: false,
          loaded: false,
          status: ResponseStatus.fail,
          data: action.payload
        };

      default: {
        return state;
      }
    }
  };

  return { ActionTypes, effect, reducer, requestAction };
}
