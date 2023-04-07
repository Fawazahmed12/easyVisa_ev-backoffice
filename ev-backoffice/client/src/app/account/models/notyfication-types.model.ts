import { EmailTemplateTypes } from '../../core/models/email-template-types.enum';

export interface NotificationCategory {
  displayName: string;
  value: EmailTemplateTypes;
}

export interface NotificationTypes {
  clientInactivity: NotificationCategory[];
  deadline: NotificationCategory[];
  importantDocuments: NotificationCategory[];
  payment: NotificationCategory[];
  blocked?: NotificationCategory[];
}
