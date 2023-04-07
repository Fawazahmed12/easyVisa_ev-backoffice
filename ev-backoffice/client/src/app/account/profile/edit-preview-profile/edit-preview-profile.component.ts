import { Component, OnInit } from '@angular/core';

import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';

import { OrganizationService, UserService } from '../../../core/services';
import { OrganizationType } from '../../../core/models/organization-type.enum';
import { Role } from '../../../core/models/role.enum';

import { OrganizationProfile } from './models/organization-profile.model';
import { EditPreviewProfileService } from './edit-preview-profile.service';

@Component({
  selector: 'app-edit-preview-profile',
  templateUrl: './edit-preview-profile.component.html',
})

export class EditPreviewProfileComponent implements OnInit {
  isOrganizationEdited$: Observable<boolean>;
  showOrganization$: Observable<boolean>;
  organizationProfile$: Observable<OrganizationProfile>;
  previewOrganizationTitle$: Observable<string>;
  isClient$: Observable<boolean>;

  constructor(
    private organizationService: OrganizationService,
    private profileService: EditPreviewProfileService,
    private userService: UserService,
  ) {
  }

  ngOnInit() {
    this.isClient$ =  this.userService.hasAccess([Role.ROLE_USER]);
    this.organizationProfile$ = this.profileService.organization$;
    this.isOrganizationEdited$ = this.organizationService.activeOrganization$.pipe(
      filter((organization) => !!organization),
      map((organization) => organization.isAdmin)
    );

    this.showOrganization$ = this.organizationService.activeOrganization$.pipe(
      filter((organization) => !!organization),
      map((organization) => organization.organizationType !== OrganizationType.SOLO_PRACTICE)
    );

    this.previewOrganizationTitle$ = this.organizationProfile$.pipe(
      filter((activeOrganization) => !!activeOrganization),
      map((activeOrganization) => this.profileService.getProfilePreviewTitle(true, activeOrganization.organizationType))
    );

  }
}
