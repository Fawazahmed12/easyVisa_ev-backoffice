import { ApplicantType } from '../../core/models/applicantType.enum';

export class DocumentAccessState {
  panelName: string;
  accessState: DocumentPortalAccessState;
}

export class DocumentPortalAccessState {
  access: boolean;
  readOnly: boolean;
}

export class RequiredApplicantDocumentModel {
  applicantId: number;
  applicantName: string;
  applicantType: ApplicantType;
  applicantTitle: string;
  direct: boolean;
  requiredDocuments: RequiredDocument[];
}

export class RequiredApplicantDocumentStateModel {
  applicantId: number;
  applicantName: string;
  applicantType: ApplicantType;
  applicantTitle: string;
  direct: boolean;
  requiredDocuments: string[];
}

export class RequiredDocument {
  id: string;
  name: string;
  helpText: string;
  isApproved: boolean;
  attachments: Attachment[];
}

export class DocumentAttachmentListState {
  id: string;
  applicantId: number;
  documentType: DocumentPanelType;
  isApproved: boolean;
  attachments: Attachment[];
}

export class Attachment {
  id: number;
  fileName: string;
  fileType: string;
  approved: boolean;
}

export enum DocumentFileType {
  pdf = 'application/pdf',
  doc = 'application/msword',
  docx = 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
  xls = 'application/vnd.ms-excel',
  xlsx = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
  jpg = 'image/jpeg',
  jpg1 = 'image/pjpeg',
  jpeg = 'image/jpeg',
  jpeg1 = 'image/pjpeg',
  png = 'image/png',
  tiff = 'image/tiff',
  tiff1 = 'image/x-tiff',
  bmp = 'image/bmp',
  bmp1 = 'image/x-windows-bmp',
  pict = 'image/pict',
  pct = 'image/x-pict'
}

export enum DocumentPanelType {
  REQUIRED_DOCUMENT = 'REQUIRED_DOCUMENT',
  DOCUMENT_SENT_TO_US = 'DOCUMENT_SENT_TO_US',
  DOCUMENT_RECEIVED_FROM_US = 'DOCUMENT_RECEIVED_FROM_US'
}


export enum DocumentFileTypeIcons {
  pdf = '../../../../assets/images/documents/pdf.png',
  doc = '../../../../assets/images/documents/doc.png',
  docx = '../../../../assets/images/documents/doc.png',
  xls = '../../../../assets/images/documents/xls.png',
  xlsx = '../../../../assets/images/documents/xls.png',
  jpg = '../../../../assets/images/documents/jpg.png',
  jpg1 = '../../../../assets/images/documents/jpg.png',
  jpeg = '../../../../assets/images/documents/jpg.png',
  jpeg1 = '../../../../assets/images/documents/jpg.png',
  png = '../../../../assets/images/documents/png.png',
  tiff = '../../../../assets/images/documents/tiff.png',
  tiff1 = '../../../../assets/images/documents/tiff.png',
  tif = '../../../../assets/images/documents/tiff.png',
  bmp = '../../../../assets/images/documents/bmp.png',
  bmp1 = '../../../../assets/images/documents/bmp.png',
  pict = '../../../../assets/images/documents/pict.png',
  pct = '../../../../assets/images/documents/pict.png',
  others = '../../../../assets/images/documents/file.png'
}

export enum DocumentImageFileType {
  jpg = 'image/jpeg',
  jpg1 = 'image/pjpeg',
  jpeg = 'image/jpeg',
  jpeg1 = 'image/pjpeg',
  png = 'image/png',
  bmp = 'image/bmp',
  bmp1 = 'image/x-windows-bmp'
}
