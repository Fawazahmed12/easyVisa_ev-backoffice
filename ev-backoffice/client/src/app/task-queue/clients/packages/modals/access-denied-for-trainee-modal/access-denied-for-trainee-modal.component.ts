import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-access-denied-for-trainee-modal',
  templateUrl: './access-denied-for-trainee-modal.component.html',
  styleUrls: ['./access-denied-for-trainee-modal.component.scss']
})
export class AccessDeniedForTraineeModalComponent implements OnInit {

  constructor(private activeModal: NgbActiveModal) {

  }

  ngOnInit(): void {
  }

  closeModal() {
    this.activeModal.close(null);
  }

}
