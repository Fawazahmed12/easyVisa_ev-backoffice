import { PackageResolverService } from './package-resolver.service';
import { TaskQueueNotificationsResolverService } from './task-queue-notifications-resolver.service';
import { ClientsPackageResolverService } from './clients-package-resolver.service';

export const RESOLVERS = [
  PackageResolverService,
  TaskQueueNotificationsResolverService,
  ClientsPackageResolverService,
];
