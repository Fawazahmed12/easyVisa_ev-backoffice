import { UserGetRequestEffects } from './get/state';
import { UserIdByEVIdGetRequestEffects } from './get-user-id-by-evid/state';
import { UserDeleteRequestEffects } from './delete-user/state';
import { ChangeMembershipPatchRequestEffects } from './change-membership/state';
import { ConvertToAttorneyPostRequestEffects } from './convert-to-attorney/state';

export const UserRequestEffects = [
  UserGetRequestEffects,
  UserDeleteRequestEffects,
  UserIdByEVIdGetRequestEffects,
  ChangeMembershipPatchRequestEffects,
  ConvertToAttorneyPostRequestEffects,
];
