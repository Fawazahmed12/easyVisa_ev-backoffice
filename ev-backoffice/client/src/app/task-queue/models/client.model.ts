import { PackageStatus } from '../../core/models/package/package-status.enum';

export class ClientModel {
  id: string;
  status: PackageStatus;
  clients: string;
  repType: string;
  legalStatus: string;
  state: string;
  benefit: string;
  ques: string;
  docs: string;
  lastActive: string;
  owed: string;
  active: boolean;
}
