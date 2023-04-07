import { Pipe, PipeTransform } from '@angular/core';

import { OrganizationType } from '../../../core/models/organization-type.enum';

@Pipe({name: 'organizationType'})
export class OrganizationTypePipe implements PipeTransform {
  transform(value: OrganizationType) {
    switch (value) {
      case OrganizationType.RECOGNIZED_ORGANIZATION: {
        return 'TEMPLATE.ORGANIZATION_TYPES.RECOGNIZED_ORGANIZATION';
      }
      case OrganizationType.LAW_FIRM: {
        return 'TEMPLATE.ORGANIZATION_TYPES.LAW_FIRM';
      }
      case OrganizationType.SOLO_PRACTICE: {
        return 'TEMPLATE.ORGANIZATION_TYPES.SOLO_PRACTICE';
      }
      default: {
        return '';
      }
    }
  }
}
