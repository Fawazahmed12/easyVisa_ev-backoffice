import { MenuOrganizationsGetRequestEffects } from './menu-organizations-get/state';
import { AffiliatedOrganizationsGetRequestEffects } from './affiliated-organizations-get/state';

export const OrganizationsRequestEffects = [
  MenuOrganizationsGetRequestEffects,
  AffiliatedOrganizationsGetRequestEffects,
];
