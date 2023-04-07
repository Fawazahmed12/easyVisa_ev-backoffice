import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { PackageStatus } from '../../../../../core/models/package/package-status.enum';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-cannot-delete-packages-modal',
  templateUrl: 'cannot-delete-packages-modal.component.html'
})
export class CannotDeletePackagesModalComponent implements OnInit {

  @Input() packageStatus: PackageStatus;

  constructor(
    private activeModal: NgbActiveModal,
    private translateService: TranslateService
  ) {
  }

  ngOnInit() {

  }

  getModalTitle() {
    if(this.packageStatus==PackageStatus.LEAD) {
      return 'TEMPLATE.TASK_QUEUE.CLIENTS.CANT_DELETE_LEADS_MODAL.TITLE';
    }
    return 'TEMPLATE.TASK_QUEUE.CLIENTS.CANT_DELETE_TRANSFERS_MODAL.TITLE';
  }

  getModalDescription(): string {
    if(this.packageStatus==PackageStatus.LEAD) {
      return this.translateService.instant('TEMPLATE.TASK_QUEUE.CLIENTS.CANT_DELETE_LEADS_MODAL.DESCRIPTION');
    }
    return  this.translateService.instant('TEMPLATE.TASK_QUEUE.CLIENTS.CANT_DELETE_TRANSFERS_MODAL.DESCRIPTION');
  }

  closeModal() {
    this.activeModal.close(null);
  }

}
