import { EmailTemplate } from '../../../core/models/email-template.model';
import { EmailTemplateTypes } from '../../../core/models/email-template-types.enum';

export const mockEmailTemplates: EmailTemplate[] = [
  {
    content: 'This is the template for the...',
    templateType: EmailTemplateTypes.COVER_LETTER_NEW,
    representativeId: '',
    subject: '',
  },
  {
    content: 'This is the template for the...',
    templateType: EmailTemplateTypes.COVER_LETTER_UPDTAED,
    representativeId: '',
    subject: '',
  },
  {
    content: 'This is the template for the...',
    templateType: EmailTemplateTypes.LEGAL_FEES_NEW,
    representativeId: '',
    subject: '',
  },
  {
    content: 'This is the template for the...',
    templateType: EmailTemplateTypes.LEGAL_FEES_UPDATED,
    representativeId: '',
    subject: '',
  },
  {
    content: 'This is the template for the...',
    templateType: EmailTemplateTypes.RETAINER_AGREEMENT_NEW,
    representativeId: '',
    subject: '',
  },
  {
    content: 'This is the template for the...',
    templateType: EmailTemplateTypes.RETAINER_AGREEMENT_UPDATED,
    representativeId: '',
    subject: '',
  },
  {
    content: 'This is the template for the...',
    templateType: EmailTemplateTypes.CLOSING_TEXT,
    representativeId: '',
    subject: '',
  },
  {
    content: 'This is the template for the...',
    templateType: EmailTemplateTypes.NEW_CLIENT,
    representativeId: '',
    subject: 'EasyVisa - Invitation To Register',
  },
  {
    content: 'This is the template for the...',
    templateType: EmailTemplateTypes.UPDATED_CLIENT,
    representativeId: '',
    subject: 'Uploaded Document Was Rejected',
  },
];
