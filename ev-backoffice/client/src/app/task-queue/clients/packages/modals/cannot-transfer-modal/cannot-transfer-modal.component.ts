import { Component, Input, OnInit } from '@angular/core';
import { OrganizationService } from '../../../../../core/services';
import { Observable } from 'rxjs';
import { Organization } from '../../../../../core/models/organization.model';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-cannot-transfer-modal',
  templateUrl: 'cannot-transfer-modal.component.html',
})
export class CannotTransferModalComponent implements OnInit {
  @Input() representativeId: string | null;
  activeOrganization$: Observable<Organization>;

  constructor(
    private organizationService: OrganizationService,
    private activeModal: NgbActiveModal,
  ) {
  }

  ngOnInit() {
    this.activeOrganization$ = this.organizationService.activeOrganization$;
  }

  closeModal() {
    this.activeModal.dismiss();
  }

}
