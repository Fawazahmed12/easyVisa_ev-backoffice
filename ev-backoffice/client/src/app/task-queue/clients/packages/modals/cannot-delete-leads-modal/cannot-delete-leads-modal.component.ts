import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-cannot-delete-leads-modal',
  templateUrl: 'cannot-delete-leads-modal.component.html'
})
export class CannotDeleteLeadsModalComponent implements OnInit {

  constructor(
    private activeModal: NgbActiveModal,
  ) {
  }

  ngOnInit() {

  }

  closeModal() {
    this.activeModal.close(null);
  }

}
