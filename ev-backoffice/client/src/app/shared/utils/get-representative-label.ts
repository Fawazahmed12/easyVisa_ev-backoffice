import { OrganizationType } from '../../core/models/organization-type.enum';

export function getRepresentativeLabel(organizationType) {
  switch (organizationType) {
    case OrganizationType.RECOGNIZED_ORGANIZATION: {
      return 'TEMPLATE.REPRESENTATIVE_TYPES.ACCREDITED_REPRESENTATIVE';
    }
    case OrganizationType.LAW_FIRM: {
      return 'TEMPLATE.REPRESENTATIVE_TYPES.ATTORNEY';
    }
    case OrganizationType.SOLO_PRACTICE: {
      return 'TEMPLATE.REPRESENTATIVE_TYPES.ATTORNEY';
    }
    default: {
      return '';
    }
  }
}
