import { Component, Input } from '@angular/core';

import { TranslateService } from '@ngx-translate/core';

import { OrganizationType } from '../../../../../core/models/organization-type.enum';

@Component({
  selector: 'app-access-denied-modal',
  templateUrl: './access-denied-modal.component.html',
})

export class AccessDeniedModalComponent {
  @Input() organisationType: OrganizationType = OrganizationType.LAW_FIRM;

  constructor(
    private translateService: TranslateService,
  ) {
  }

  get organizationTypeTranslationPath() {
    switch (this.organisationType) {
      case OrganizationType.LAW_FIRM: {
        return 'TEMPLATE.ORGANIZATION_TYPES.LAW_FIRM';
      }
      case OrganizationType.RECOGNIZED_ORGANIZATION: {
        return 'TEMPLATE.ORGANIZATION_TYPES.RECOGNIZED_ORGANIZATION';
      }
      case OrganizationType.SOLO_PRACTICE: {
        return 'TEMPLATE.ORGANIZATION_TYPES.SOLO_PRACTICE';
      }
      default: {
        return '';
      }
    }
  }

  get descriptionFirst() {
    // Used if else operator instead of ternary to reduce memory usage;
    let associatedWith = '';
    if (this.organisationType === OrganizationType.SOLO_PRACTICE) {
      associatedWith = this.translateService.instant(
        'TEMPLATE.TASK_QUEUE.ACCESS_DENIED_MODAL.ASSOCIATED_WITH_SOLO'
      );
    } else {
      associatedWith = this.translateService.instant(
        'TEMPLATE.TASK_QUEUE.ACCESS_DENIED_MODAL.ASSOCIATED_WITH_MEMBER',
        { organizationType: this.translateService.instant(this.organizationTypeTranslationPath) }
      );
    }
    return this.translateService.instant(
      'TEMPLATE.TASK_QUEUE.ACCESS_DENIED_MODAL.DESCRIPTION_P_1',
      { associatedWith }
    );
  }

  get descriptionSecond() {
    // Used if operator instead of ternary to reduce memory usage;
    let orMemberOf = '';
    if (this.organisationType !== OrganizationType.SOLO_PRACTICE) {
      orMemberOf = this.translateService.instant(
        'TEMPLATE.TASK_QUEUE.ACCESS_DENIED_MODAL.OR_MEMBER_OF',
        { organizationType: this.translateService.instant(this.organizationTypeTranslationPath) }
      );
    }
    return this.translateService.instant(
      'TEMPLATE.TASK_QUEUE.ACCESS_DENIED_MODAL.DESCRIPTION_P_2',
      { orMemberOf }
    );
  }
}
