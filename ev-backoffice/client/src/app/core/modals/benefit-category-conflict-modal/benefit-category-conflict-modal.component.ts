import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-benefit-category-conflict-modal',
  templateUrl: './benefit-category-conflict-modal.component.html',
})

export class BenefitCategoryConflictModalComponent {
  @Input() content: string;

  constructor(
    private activeModal: NgbActiveModal,
  ) {
  }

  closeModal() {
    this.activeModal.dismiss();
  }
}
