import { createFeatureSelector, createSelector } from '@ngrx/store';
import { createEntityAdapter, EntityAdapter, EntityState } from '@ngrx/entity';
import { OrganizationEmployee } from '../../models/organization-employee.model';


export const PERMISSIONS = 'Permissions';

export interface PermissionsState extends EntityState<OrganizationEmployee> {
  activePermissionId: number;
}
export const adapter: EntityAdapter<OrganizationEmployee> = createEntityAdapter<OrganizationEmployee>({
  selectId: (myEntity: OrganizationEmployee) => myEntity.employeeId
});

export const {selectAll, selectEntities} = adapter.getSelectors();

export const selectPermissions = selectAll;

export const selectPermissionsEntities = selectEntities;

export const selectPermissionsState = createFeatureSelector<PermissionsState>(PERMISSIONS);

export const selectActivePermissionId = ({activePermissionId}: PermissionsState) => activePermissionId;

export const selectActivePermission = createSelector(
  selectPermissionsEntities,
  selectActivePermissionId,
  (permissionsEntities, activeOrganizationEmployeeId) => permissionsEntities[activeOrganizationEmployeeId]
);
