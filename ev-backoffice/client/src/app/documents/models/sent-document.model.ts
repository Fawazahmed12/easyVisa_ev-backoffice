import { ApplicantType } from '../../core/models/applicantType.enum';
import { Attachment } from './documents.model';

export class ApplicantSentDocuments {
  applicantId: number;
  applicantName: string;
  applicantType: ApplicantType;
  applicantTitle: string;
  direct: boolean;
  sentDocuments: SentDocument[];
}

export class SentDocument {
  id: string;
  name: string;
  actionDate: string;
  isApproved: boolean;
  attachments: Attachment[];
}
