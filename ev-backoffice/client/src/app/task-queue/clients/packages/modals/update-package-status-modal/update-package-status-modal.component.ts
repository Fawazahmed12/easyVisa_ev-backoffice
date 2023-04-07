import { Component, Input, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { EMPTY, Observable } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { PackageStatus } from '../../../../../core/models/package/package-status.enum';
import { ModalService, OrganizationService, PackagesService } from '../../../../../core/services';
import { OrganizationType } from '../../../../../core/models/organization-type.enum';
import { Package } from '../../../../../core/models/package/package.model';

@Component({
  selector: 'app-update-package-status-modal',
  templateUrl: './update-package-status-modal.component.html',
})

export class UpdatePackageStatusModalComponent implements OnInit {
  @Input() item: Package;
  @Input() selectedStatus: PackageStatus;

  organizationType$: Observable<OrganizationType>;

  title: string;
  isSelectedStatusBlocked: boolean;
  isSelectedStatusOpen: boolean;
  isSelectedStatusClosed: boolean;

  constructor(
    private activeModal: NgbActiveModal,
    private modalService: ModalService,
    private packagesService: PackagesService,
    private organizationService: OrganizationService,
  ) {
  }

  ngOnInit() {
    this.organizationType$ = this.organizationService.activeOrganization$.pipe(
      map((organization) => organization.organizationType)
    );
    this.isSelectedStatusBlocked = this.selectedStatus === PackageStatus.BLOCKED;
    this.isSelectedStatusOpen = this.selectedStatus === PackageStatus.OPEN;
    this.isSelectedStatusClosed = this.selectedStatus === PackageStatus.CLOSED;
    this.title = this.getModalTitle();
  }

  modalDismiss() {
    this.activeModal.dismiss();
  }

  updateStatus() {
    this.packagesService.updatePackageStatus({id: this.item.id, newStatus: this.selectedStatus}).pipe(
      catchError((error: HttpErrorResponse) => {
          if (error.status !== 401) {
            this.modalService.showErrorModal(error.error.errors || [error.error]);
          }
          return EMPTY;
        }
      ),
    );
    this.modalDismiss();
  }

  getModalTitle() {
    switch (this.selectedStatus) {
      case PackageStatus.BLOCKED: {
        return 'TEMPLATE.TASK_QUEUE.CLIENTS.BLOCK_PACKAGE_MODAL.TITLE';
      }
      case PackageStatus.OPEN: {
        return 'TEMPLATE.TASK_QUEUE.CLIENTS.RE_OPEN_MODAL.TITLE';
      }
      case PackageStatus.CLOSED: {
        return 'TEMPLATE.TASK_QUEUE.CLIENTS.CLOSE_PACKAGE_MODAL.TITLE';
      }
      default: {
        return '';
      }
    }
  }
}
