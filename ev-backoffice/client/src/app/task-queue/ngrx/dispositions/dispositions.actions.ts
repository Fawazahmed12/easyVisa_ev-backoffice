import { Action } from '@ngrx/store';

import { Disposition } from '../../models/dispositions.model';

import { DISPOSITIONS } from './dispositions.state';
import { HttpResponse } from '@angular/common/http';

export const DispositionsActionTypes = {
  GetDispositions: `[${DISPOSITIONS}] Get Alerts`,
  GetDispositionsSuccess: `[${DISPOSITIONS}] Get Alerts Success`,
  GetDispositionsFailure: `[${DISPOSITIONS}] Get Alerts Failure`,
  GetDispositionData: `[${DISPOSITIONS}] Get Disposition Data`,
  GetDispositionDataSuccess: `[${DISPOSITIONS}] Get Disposition Data Success`,
  GetDispositionDataFailure: `[${DISPOSITIONS}] Get Disposition Data Failure`,
  PutDisposition: `[${DISPOSITIONS}] Put Disposition`,
  PutDispositionSuccess: `[${DISPOSITIONS}] Put Disposition Success`,
  PutDispositionFailure: `[${DISPOSITIONS}] Put Disposition Failure`,
  SetActiveDisposition: `[${DISPOSITIONS}] Set Active Alert`,
  DownloadActiveDispositionData: `[${DISPOSITIONS}] Download Active Disposition Data`,
  SetPreviousActiveDispositionId: `[${DISPOSITIONS}] Set Previous Active DispositionId`,
  SetNextActiveDispositionId: `[${DISPOSITIONS}] Set Next Active Disposition Id`,
  ApproveActiveDisposition: `[${DISPOSITIONS}] Approve Active Disposition`,
  RemoveDisposition: `[${DISPOSITIONS}] Remove Disposition`,
  RejectDisposition: `[${DISPOSITIONS}] Reject Disposition`,
  ResetActiveDisposition: `[${DISPOSITIONS}] Reset Active Disposition`,
  ResetDispositions: `[${DISPOSITIONS}] Reset Dispositions`,
};


export class GetDispositions implements Action {
  readonly type = DispositionsActionTypes.GetDispositions;

  constructor(public payload?: {sort?: string; order?: string; representativeId: number}) {
  }
}

export class GetDispositionsSuccess implements Action {
  readonly type = DispositionsActionTypes.GetDispositionsSuccess;

  constructor(public payload: {body: Disposition[]; xTotalCount: string}) {
  }
}

export class GetDispositionsFailure implements Action {
  readonly type = DispositionsActionTypes.GetDispositionsFailure;

  constructor(public payload?: any) {
  }
}

export class SetActiveDisposition implements Action {
  readonly type = DispositionsActionTypes.SetActiveDisposition;

  constructor(public payload: number) {
  }
}

export class GetDispositionData implements Action {
  readonly type = DispositionsActionTypes.GetDispositionData;

  constructor(public payload: any) {
  }
}

export class GetDispositionDataSuccess implements Action {
  readonly type = DispositionsActionTypes.GetDispositionDataSuccess;

  constructor(public payload?: any) {
  }
}

export class GetDispositionDataFailure implements Action {
  readonly type = DispositionsActionTypes.GetDispositionDataFailure;

  constructor(public payload?: any) {
  }
}

export class PutDisposition implements Action {
  readonly type = DispositionsActionTypes.PutDisposition;

  constructor(public payload: any) {
  }
}

export class PutDispositionSuccess implements Action {
  readonly type = DispositionsActionTypes.PutDispositionSuccess;

  constructor(public payload: any) {
  }
}

export class PutDispositionFailure implements Action {
  readonly type = DispositionsActionTypes.PutDispositionFailure;

  constructor(public payload?: any) {
  }
}

export class DownloadActiveDispositionData implements Action {
  readonly type = DispositionsActionTypes.DownloadActiveDispositionData;
}

export class SetNextActiveDispositionId implements Action {
  readonly type = DispositionsActionTypes.SetNextActiveDispositionId;
}

export class SetPreviousActiveDispositionId implements Action {
  readonly type = DispositionsActionTypes.SetPreviousActiveDispositionId;
}

export class ApproveActiveDisposition implements Action {
  readonly type = DispositionsActionTypes.ApproveActiveDisposition;

  constructor(public payload: {approve: boolean; organizationId: number; representativeId: number}) {
  }
}

export class RejectDisposition implements Action {
  readonly type = DispositionsActionTypes.RejectDisposition;

  constructor(public payload: {
    approve: boolean;
    rejectionMailMessage: string;
    rejectionMailSubject: string;
    organizationId: number;
    representativeId: number;
  }) {
  }
}

export class RemoveDisposition implements Action {
  readonly type = DispositionsActionTypes.RemoveDisposition;

  constructor(public payload: any) {
  }
}
export class ResetActiveDisposition implements Action {
  readonly type = DispositionsActionTypes.ResetActiveDisposition;
}

export class ResetDispositions implements Action {
  readonly type = DispositionsActionTypes.ResetDispositions;
}

export type DispositionsActionsUnion =
  | GetDispositions
  | GetDispositionsSuccess
  | GetDispositionsFailure
  | GetDispositionData
  | GetDispositionDataSuccess
  | GetDispositionDataFailure
  | PutDisposition
  | PutDispositionSuccess
  | PutDispositionFailure
  | SetActiveDisposition
  | DownloadActiveDispositionData
  | SetNextActiveDispositionId
  | SetPreviousActiveDispositionId
  | ApproveActiveDisposition
  | RemoveDisposition
  | RejectDisposition
  | ResetDispositions
  | ResetActiveDisposition;
