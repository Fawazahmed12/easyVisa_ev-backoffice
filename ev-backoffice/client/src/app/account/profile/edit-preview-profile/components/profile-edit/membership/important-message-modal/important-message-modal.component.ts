import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';

import { EMPTY, Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { FeeDetails } from '../../../../../../../core/models/fee-details.model';
import { ConfigDataService, ModalService } from '../../../../../../../core/services';
import { ConfirmButtonType } from '../../../../../../../core/modals/confirm-modal/confirm-modal.component';

@Component({
  selector: 'app-important-message-modal',
  templateUrl: './important-message-modal.component.html',
})

export class ImportantMessageModalComponent implements OnInit {

  @ViewChild('referralBonus', { static: true }) referralBonus;
  feeDetails$: Observable<FeeDetails>;

  constructor(
    private activeModal: NgbActiveModal,
    private router: Router,
    private configDataService: ConfigDataService,
    private modalService: ModalService
  ) {

  }

  ngOnInit() {
    this.feeDetails$ = this.configDataService.feeDetails$;
  }

  modalDismiss() {
    this.activeModal.dismiss();
  }

  redirectToDashboard() {
    this.activeModal.dismiss();
    this.openReferralBonusModal();
    this.router.navigate([ '/dashboard/financial' ]);
  }

  redirectToInviteColleagues() {
    this.activeModal.dismiss();
    this.router.navigate([ '/dashboard/financial/invite-colleagues' ]);
  }

  gotoInviteColleagues() {
    this.modalService.closeAllModals();
    this.router.navigate([ '/dashboard/financial/invite-colleagues' ]);
  }

  openReferralBonusModal() {
    const buttons = [
      {
        label: 'FORM.BUTTON.OK',
        type: ConfirmButtonType.Dismiss,
        className: 'btn btn-primary mr-2 min-w-100',
      }
    ];

    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.DASHBOARD.FINANCIAL.REFERRAL_BONUSES',
      body: this.referralBonus,
      buttons,
      centered: true,
      size: 'lg',
    }).pipe(
      catchError(() => EMPTY)
    );
  }

}
