import { Component, TemplateRef } from '@angular/core';

import { ModalDismissReasons, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

export enum ConfirmButtonType {
  Dismiss = 'dismiss',
  Close = 'close',
  Custom = 'custom',
}

export enum ConfirmModalDismissReason {
  BackdropClick = ModalDismissReasons.BACKDROP_CLICK,
  EscClick = ModalDismissReasons.ESC,
  CloseIconClick = 3,
}

export interface ConfirmButton {
  label: string;
  type: ConfirmButtonType;
  className?: string;
  value?: any;
  action?: (value: any) => any;
}

export const OkButton: ConfirmButton = {
  label: 'TEMPLATE.MODAL.OK',
  type: ConfirmButtonType.Close,
  className: 'btn btn-primary',
};

export const OkButtonLg: ConfirmButton = {
  label: 'TEMPLATE.MODAL.OK',
  type: ConfirmButtonType.Dismiss,
  className: 'btn btn-primary mr-2 min-w-100',
};


@Component({
  selector: 'app-confirm-modal',
  templateUrl: 'confirm-modal.component.html',
})
export class ConfirmModalComponent {

  showCloseIcon = true;
  header: string;
  body: string | any[] | TemplateRef<any>;
  size: 'sm' | 'lg';
  windowClass: string;
  centered: boolean;
  buttons: ConfirmButton[];

  constructor(private activeModal: NgbActiveModal) {
  }

  get isBodyTemplate() {
    return this.body instanceof TemplateRef;
  }

  get isBodyArray() {
    return this.body instanceof Array;
  }

  onCloseIconClick() {
    this.activeModal.dismiss(ConfirmModalDismissReason.CloseIconClick);
  }

  onButtonClick(button: ConfirmButton) {
    switch (button.type) {
      case ConfirmButtonType.Close: {
        this.activeModal.close(button.value);
        break;
      }
      case ConfirmButtonType.Dismiss: {
        this.activeModal.dismiss(button.value);
        break;
      }
      case ConfirmButtonType.Custom: {
        button.action(button);
        break;
      }
    }
  }
}
