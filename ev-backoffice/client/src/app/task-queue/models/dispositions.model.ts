import { BenefitCategories } from '../../core/models/benefit-categories.enum';

export class Disposition {
  id: number;
  applicantId: number;
  applicantName: string;
  representativeId: number;
  representativeName: string;
  fileName: string;
  document: string;
  benefitCategory: BenefitCategories;
  read: boolean;
  createdDate: string;
  panelName: string;
}
