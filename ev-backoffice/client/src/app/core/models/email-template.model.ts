export interface EmailTemplate {
  templateType: string;
  content: string;
  representativeId: string;
  subject: string;
  isDefault?: boolean;
}
