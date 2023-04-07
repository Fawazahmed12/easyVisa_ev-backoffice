import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-confirm-delete-leads-modal',
  templateUrl: 'confirm-delete-leads-modal.component.html'
})
export class ConfirmDeleteLeadsModalComponent implements OnInit {

  @Input() selectedPackageIds;

  constructor(
    private activeModal: NgbActiveModal
  ) {
  }

  ngOnInit() {

  }

  dismissModal() {
    this.activeModal.dismiss('Close');
  }

  confirmDeletePackages() {
    this.activeModal.close([ ...this.selectedPackageIds ]);
  }

}
