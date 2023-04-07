import { OrganizationsRequestState } from './state';

import { menuOrganizationsRequestReducer } from './menu-organizations-get/state';
import { affiliatedOrganizationsGetRequestHandler, affiliatedOrganizationsRequestReducer } from './affiliated-organizations-get/state';

export function reducer(state: OrganizationsRequestState = {}, action): OrganizationsRequestState {
  return {
    menuOrganizationsGet: menuOrganizationsRequestReducer(state.menuOrganizationsGet, action),
    affiliatedOrganizationsGet: affiliatedOrganizationsRequestReducer(state.affiliatedOrganizationsGet, action),
  };
}
