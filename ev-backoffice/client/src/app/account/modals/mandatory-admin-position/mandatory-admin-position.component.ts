import { Component, OnInit } from '@angular/core';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';

import { OrganizationService } from '../../../core/services';
import { Organization } from '../../../core/models/organization.model';


@Component({
  selector: 'app-mandatory-admin-position',
  templateUrl: './mandatory-admin-position.component.html',
})

export class MandatoryAdminPositionComponent implements OnInit {
  activeOrganization$: Observable<Organization>;


  constructor(
    private activeModal: NgbActiveModal,
    private organizationService: OrganizationService,
  ) {
  }

  ngOnInit() {
    this.activeOrganization$ = this.organizationService.activeOrganization$;
  }

  closeModal() {
    this.activeModal.close();
  }
}
