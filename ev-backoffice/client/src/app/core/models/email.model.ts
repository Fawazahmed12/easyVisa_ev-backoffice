export interface Email {
  id?: string;
  templateType: string;
  content: string;
  packageId?: string;
  representativeId: string;
  subject: string;
  responseMessage?: string;
}
