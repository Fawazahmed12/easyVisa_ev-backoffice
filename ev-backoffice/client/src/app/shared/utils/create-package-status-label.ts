import { PackageStatus } from '../../core/models/package/package-status.enum';

export function createPackageStatusLabel(status) {
  switch (status) {
    case PackageStatus.OPEN: {
      return 'TEMPLATE.PACKAGE_STATUS.OPEN';
    }
    case PackageStatus.LEAD: {
      return 'TEMPLATE.PACKAGE_STATUS.LEAD';
    }
    case PackageStatus.BLOCKED: {
      return 'TEMPLATE.PACKAGE_STATUS.BLOCKED';
    }
    case PackageStatus.CLOSED: {
      return 'TEMPLATE.PACKAGE_STATUS.CLOSED';
    }
    case PackageStatus.TRANSFERRED: {
      return 'TEMPLATE.PACKAGE_STATUS.TRANSFERRED';
    }
    default: {
      return '';
    }
  }
}

