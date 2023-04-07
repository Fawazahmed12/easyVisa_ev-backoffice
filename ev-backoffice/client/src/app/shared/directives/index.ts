import { HasRoleDirectiveModule } from './has-role/has-role-directive.module';
import { IfActiveOrganizationDirectiveModule } from './if-active-organization/if-active-organization-directive.module';
import { IfActiveUserDirectiveModule } from './if-active-user/if-active-user-directive.module';
import { HideByPositionDirectiveModule } from './hide-by-position/hide-by-position-directive.module';
import { InsertComponentDirectiveModule } from './insert-component/insert-component-directive.module';
import { IfNoRepresentativeSelectedDirectiveModule } from './if-no-representative-selected/if-no-representative-selected-directive.module';
import { IfCurrentRepresentativeNotAdminMeDirectiveModule } from './if-current-representative-not-me/if-current-representative-not-admin-me-directive.module';
import { IfTabLoadingDirectiveModule } from './if-tab-loading/if-tab-loading-directive.module';

export const DIRECTIVES = [
  HasRoleDirectiveModule,
  HideByPositionDirectiveModule,
  IfActiveOrganizationDirectiveModule,
  IfActiveUserDirectiveModule,
  InsertComponentDirectiveModule,
  IfNoRepresentativeSelectedDirectiveModule,
  IfCurrentRepresentativeNotAdminMeDirectiveModule,
  IfTabLoadingDirectiveModule,
];
