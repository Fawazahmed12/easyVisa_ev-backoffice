import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { PackageStatus } from '../../../../../core/models/package/package-status.enum';
import { I18nService } from '../../../../../core/i18n/i18n.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-confirm-delete-packages-modal',
  templateUrl: 'confirm-delete-packages-modal.component.html'
})
export class ConfirmDeletePackagesModalComponent implements OnInit {

  @Input() selectedPackageIds;
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
      return 'TEMPLATE.TASK_QUEUE.CLIENTS.CONFIRM_DELETE_LEADS_MODAL.TITLE';
    }
    return 'TEMPLATE.TASK_QUEUE.CLIENTS.CONFIRM_DELETE_TRANSFERS_MODAL.TITLE';
  }

  getModalDescription(): string {
    if(this.packageStatus==PackageStatus.LEAD) {
      return this.translateService.instant('TEMPLATE.TASK_QUEUE.CLIENTS.CONFIRM_DELETE_LEADS_MODAL.DESCRIPTION');
    }
    return this.translateService.instant('TEMPLATE.TASK_QUEUE.CLIENTS.CONFIRM_DELETE_TRANSFERS_MODAL.DESCRIPTION');
  }

  dismissModal() {
    this.activeModal.dismiss('Close');
  }

  confirmDeletePackages() {
    this.activeModal.close([ ...this.selectedPackageIds ]);
  }
}
