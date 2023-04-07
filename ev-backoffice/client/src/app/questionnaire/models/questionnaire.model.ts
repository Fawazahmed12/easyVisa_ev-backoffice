import { ApplicantType } from '../../core/models/applicantType.enum';

export class QuestionnaireAccessState {
  access: boolean;
  readOnly: boolean;
}

export class QuestionnaireModel {
  applicantId: number;
  applicantName: string;
  applicantType: ApplicantType;
  applicantTitle: string;
  benefitCategoryName: string;
  direct: boolean;
  completedWeightage: number;
  sections: Section[];
}

export class QuestionnaireStateModel {
  applicantId: number;
  applicantName: string;
  applicantType: ApplicantType;
  applicantTitle: string;
  direct: boolean;
  sections: string[];
}

export class Section {
  id: string;
  displayText: string;
  shortName: string;
  completionState: boolean;
  completedPercentage: number;
  hasCompletionWarningRule: boolean;
}

export interface Answer {
  [ key: string ]: Answer;
}

export interface QuestionnaireParam {
  packageId: number;
  applicantId: string;
  sectionId: string;
}

export class AnswerModel {
  value: string;
  applicantId: number;
  packageId: number;
  sectionId: string;
  subsectionId: string;
  questionId: string;
  index?: number; // will hold repeating answers index
}



export class AnswerSaveRequestItem {
  value: string;
  subsectionId: string;
  questionId: string;
  index?: number; // will hold repeating answers index
  hasAnswerCompleted: boolean;
}


export class RepeatGroupAddRequestItem {
  repeatingGroupId: string;
  subsectionId: string;
}


export class RepeatGroupRemoveRequestItem {
  repeatingGroupId: string;
  subsectionId: string;
  index: number; // will hold repeating group index
}

export class RepeatGroupModel {
  applicantId: number;
  packageId: number;
  sectionId: string;
  subsectionId: string;
  repeatingGroupId: string;
  index?: number; // will hold repeating group index, which is available only for remove group action
}

export class SectionWarningModel {
  hasSectionWarning: boolean;
  headerText: string;
  warningMessage: string;
  leftButtonText: string;
  rightButtonText: string;
}

export class AnswerValidationModel {
  hasAnswerCompleted: boolean;
  hasValidAnswer: boolean;
  errorMessage: string;
  value: string;
  resetValue: string;
  subsectionId: string;
  questionId: string;
  index?: number; // will hold repeating answers index
}
