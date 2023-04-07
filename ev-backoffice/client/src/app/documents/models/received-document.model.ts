import { ApplicantType } from '../../core/models/applicantType.enum';
import { Attachment } from './documents.model';

export class ApplicantReceivedDocuments {
  applicantId: number;
  applicantName: string;
  applicantType: ApplicantType;
  applicantTitle: string;
  direct: boolean;
  receivedDocuments: ReceivedDocument[];
}

export class ReceivedDocument {
  receivedDocumentType: string;
  description: string;
  actionDate: string;
  isApproved: boolean;
  attachments: Attachment[];
}
