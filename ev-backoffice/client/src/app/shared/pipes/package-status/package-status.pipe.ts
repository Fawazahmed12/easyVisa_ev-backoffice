import { Pipe, PipeTransform } from '@angular/core';

import { PackageStatus } from '../../../core/models/package/package-status.enum';

import { createPackageStatusLabel } from '../../utils/create-package-status-label';


@Pipe({name: 'packageStatus'})
export class PackageStatusPipe implements PipeTransform {
  transform(value: PackageStatus) {
    return createPackageStatusLabel(value);
  }
}
