export enum PackageStatus {
  BLOCKED = 'BLOCKED',
  LEAD = 'LEAD',
  OPEN = 'OPEN',
  CLOSED = 'CLOSED',
  TRANSFERRED = 'TRANSFERRED',
  DELETED = 'DELETED',
}

export interface PackageStatusValue {
  label: string;
  value: PackageStatus;
}

export const packageStatusValue: PackageStatusValue[] = [
  {
    label: 'TEMPLATE.TASK_QUEUE.CLIENTS.OPEN',
    value: PackageStatus.OPEN,
  },
  {
    label: 'TEMPLATE.TASK_QUEUE.CLIENTS.BLOCKED',
    value: PackageStatus.BLOCKED,
  },
  {
    label: 'TEMPLATE.TASK_QUEUE.CLIENTS.CLOSED',
    value: PackageStatus.CLOSED,
  },
  {
    label: 'TEMPLATE.TASK_QUEUE.CLIENTS.LEAD',
    value: PackageStatus.LEAD,
  },
  {
    label: 'TEMPLATE.TASK_QUEUE.CLIENTS.TRANSFERRED',
    value: PackageStatus.TRANSFERRED,
  },
  {
    label: 'TEMPLATE.TASK_QUEUE.CLIENTS.DELETED',
    value: PackageStatus.DELETED,
  },
];
