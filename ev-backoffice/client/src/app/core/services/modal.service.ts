import { Injectable, TemplateRef } from '@angular/core';

import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { fromPromise } from 'rxjs/internal-compatibility';

import { ConfirmButton, ConfirmModalComponent, OkButton } from '../modals/confirm-modal/confirm-modal.component';

@Injectable()
export class ModalService {

  constructor(
    public ngbModal: NgbModal,
  ) {
  }

  openConfirmModal(options: {
    header?: string;
    body: string | any[] | TemplateRef<any>;
    buttons: ConfirmButton[];
    showCloseIcon?: boolean;
    size?: 'sm' | 'lg';
    windowClass?: string;
    centered?: boolean;
    backdrop?: boolean | 'static';
  }) {
    const modalRef = this.ngbModal.open(ConfirmModalComponent, options);
    this.setOptions(options, modalRef.componentInstance);
    return fromPromise(modalRef.result);
  }

  showErrorModal(errors) {
    return this.openConfirmModal({
      header: 'TEMPLATE.MODAL.ERROR_TITLE',
      body: errors,
      buttons: [OkButton],
    });
  }

  openModal(type) {
    this.ngbModal.open(type, {
        centered: true,
      }
    );
  }

  closeAllModals() {
    this.ngbModal.dismissAll();
  }

  private setOptions(options, componentInstance) {
    for (const option in options) {
      if (typeof options[option] !== 'undefined') {
        componentInstance[option] = options[option];
      }
    }
  }

}
