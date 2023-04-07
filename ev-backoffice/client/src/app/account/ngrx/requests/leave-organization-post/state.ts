import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';

import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { head } from 'lodash-es';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { fromPromise } from 'rxjs/internal-compatibility';

import { createRequestHandler, RequestFailAction, RequestSuccessAction } from '../../../../core/ngrx/utils';
import { GetMenuOrganizations } from '../../../../core/ngrx/organizations/organizations.actions';
import { AttorneyProfile } from '../../../profile/edit-preview-profile/models/attorney-profile.model';


import { AccountModuleRequestService } from '../request.service';
import { OkButton } from '../../../../core/modals/confirm-modal/confirm-modal.component';
import { ModalService } from '../../../../core/services';
import { LeaveOrganizationErrorType } from '../../../models/leave-organization-error-type.enum';
import { MandatoryAdminPositionComponent } from '../../../modals/mandatory-admin-position/mandatory-admin-position.component';

export const leaveOrganizationPostRequestHandler = createRequestHandler('PostLeaveOrganizationRequest');

export function leaveOrganizationPostRequestReducer(state, action) {
  return leaveOrganizationPostRequestHandler.reducer(state, action);
}

@Injectable()
export class LeaveOrganizationPostRequestEffects {

  @Effect()
  leaveOrganization$: Observable<Action> = leaveOrganizationPostRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.leaveOrganizationPostRequest.bind(this.accountModuleRequestService)
  );

  @Effect()
  leaveOrganizationSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(leaveOrganizationPostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({}: RequestSuccessAction<AttorneyProfile>) => new GetMenuOrganizations())
  );

  @Effect({dispatch: false})
  leaveOrganizationFail$: Observable<Action> = this.actions$.pipe(
    ofType(leaveOrganizationPostRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      const errorForLeaveOrganizationModal = head(payload.error.errors);
      if (!!errorForLeaveOrganizationModal.type) {
        switch (errorForLeaveOrganizationModal.type) {
          case LeaveOrganizationErrorType.ALONE_ADMIN: {
              const modalRef = this.ngbModal.open(MandatoryAdminPositionComponent, {
                centered: true,
              });
              modalRef.componentInstance.isRequest = false;
              return fromPromise(modalRef.result);
          }
          case LeaveOrganizationErrorType.ASSIGNED_PACKAGES: {
            return this.modalService.openConfirmModal({
              header: 'TEMPLATE.ACCOUNT.PROFILE.LEAVE_ORGANIZATION_ERROR_ASSIGNED_PACKAGES.TITLE',
              body: 'TEMPLATE.ACCOUNT.PROFILE.LEAVE_ORGANIZATION_ERROR_ASSIGNED_PACKAGES.P_1',
              centered: true,
              buttons: [OkButton],
            });
          }
        }
      } else if (!!errorForLeaveOrganizationModal.message) {
        return this.modalService.showErrorModal(payload.error.errors);
      } else {
        return;
      }
    })
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
    private modalService: ModalService,
    private ngbModal: NgbModal,
  ) {
  }
}
