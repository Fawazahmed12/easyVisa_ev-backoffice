import { Role } from '../../core/models/role.enum';

export function rolesHasAccess(userRoles: Role[], roles: Role[] = []) {
  return userRoles.some((userRole: Role) => roles.includes(userRole));
}
