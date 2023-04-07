import { EmailTemplateTypes } from '../../core/models/email-template-types.enum';

export interface Reminder {
  id: number;
  content: string;
  subject?: string;
  repeatInterval: number;
  templateType: EmailTemplateTypes;
  daysAfter?: number;
}
