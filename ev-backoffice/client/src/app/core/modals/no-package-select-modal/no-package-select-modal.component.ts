import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';

import { Role } from '../../models/role.enum';
import { UserService } from '../../services';

@Component({
  selector: 'app-no-package-select-modal',
  templateUrl: './no-package-select-modal.component.html'
})
export class NoPackageSelectModalComponent implements OnInit {

  isUser$: Observable<boolean>;

  constructor(public activeModal: NgbActiveModal,
              private router: Router,
              private userService: UserService) {
  }

  ngOnInit() {
    this.isUser$ = this.userService.hasAccess([Role.ROLE_USER]);
  }

  gotoClients() {
    this.activeModal.dismiss();
    this.router.navigate(['task-queue', 'clients']);
  }

  gotoProgressStatus() {
    this.activeModal.dismiss();
    this.router.navigate(['dashboard', 'progress-status']);
  }

  modalDismiss() {
    this.activeModal.dismiss();
  }

}
