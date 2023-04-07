import { InviteMemberPutRequestEffects } from './invite-member-put/state';
import { PermissionsGetRequestEffects } from './permissions-get/state';
import { VerifyMemberPostRequestEffects } from './verify-member-post/state';
import { CreateEmployeePostRequestEffects } from './create-employee-post/state';
import { PermissionGetRequestEffects } from './permission-get/state';
import { UpdateEmployeePutRequestEffects } from './update-employee-put/state';
import { InviteDeleteRequestEffects } from './invite-delete/state';

export const PermissionsModuleRequestEffects = [
  InviteMemberPutRequestEffects,
  VerifyMemberPostRequestEffects,
  CreateEmployeePostRequestEffects,
  UpdateEmployeePutRequestEffects,
  PermissionsGetRequestEffects,
  PermissionGetRequestEffects,
  InviteDeleteRequestEffects,
];
