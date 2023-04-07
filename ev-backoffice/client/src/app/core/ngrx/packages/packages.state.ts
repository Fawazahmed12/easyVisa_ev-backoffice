import { createEntityAdapter, EntityAdapter, EntityState } from '@ngrx/entity';

import { createFeatureSelector, createSelector } from '@ngrx/store';

import { Package } from '../../models/package/package.model';

export const PACKAGES = 'Packages';

export interface PackagesState extends EntityState<Package> {
  activePackage: Package;
  activePackageId: number;
  currentPackageId: string;
  total: number;
}

export const adapter: EntityAdapter<Package> = createEntityAdapter<Package>();

export const {selectAll, selectEntities} = adapter.getSelectors();

export const selectPackages = selectAll;

export const selectPackagesEntities = selectEntities;

export const selectPackagesState = createFeatureSelector<PackagesState>(PACKAGES);

export const selectCurrentPackageId = ({currentPackageId}: PackagesState) => currentPackageId;

export const selectActivePackage = ({activePackage}: PackagesState) => activePackage;

export const selectActivePackageId = ({activePackageId}: PackagesState) => activePackageId;

export const selectPackagesTotal = ({total}: PackagesState) => total;


export const getPackagesTotal = createSelector(
  selectPackagesState,
  selectPackagesTotal,
);

export const getPackagesData = createSelector(
  selectPackagesState,
  selectPackages,
);

export const getCurrentPackageId = createSelector(
  selectPackagesState,
  selectCurrentPackageId,
);

export const getPackagesEntities = createSelector(
  selectPackagesState,
  selectPackagesEntities,
);

export const selectCurrentPackage = createSelector(
  getPackagesEntities,
  getCurrentPackageId,
  (packagesEntities, packageId) => packagesEntities[packageId]
);

export const getActivePackage = createSelector(
  selectPackagesState,
  selectActivePackage,
);

export const getActivePackageId = createSelector(
  selectPackagesState,
  selectActivePackageId,
);
