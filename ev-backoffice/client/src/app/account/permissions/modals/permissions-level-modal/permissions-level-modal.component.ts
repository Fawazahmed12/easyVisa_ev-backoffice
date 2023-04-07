import { Component, OnInit } from '@angular/core';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';

import { OrganizationService, UserService } from '../../../../core/services';
import { Organization } from '../../../../core/models/organization.model';
import { OrganizationType } from '../../../../core/models/organization-type.enum';


@Component({
  selector: 'app-permissions-level',
  templateUrl: './permissions-level-modal.component.html',
  styleUrls: ['./permissions-level-modal.component.scss'],
})

export class PermissionsLevelModalComponent implements OnInit {
  activeOrganization$: Observable<Organization>;
  isSoloPractitioner$: Observable<boolean>;

  constructor(
    private activeModal: NgbActiveModal,
    private userService: UserService,
    private organizationService: OrganizationService,
  ) {
  }

  ngOnInit() {
    this.activeOrganization$ = this.organizationService.activeOrganization$;

    this.isSoloPractitioner$ = this.activeOrganization$.pipe(
      filter((activeOrganization) => !!activeOrganization),
      map((organization) => organization.organizationType === OrganizationType.SOLO_PRACTICE)
    );
  }

  closeModal() {
    this.activeModal.close();
  }
}
