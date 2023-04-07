export class FormsSheets {
  packageApplicants: FormsPackageApplicant[];
  packageForms: PackageForm[];
  packageContinuationSheets: PackageContinuationSheets[];
}

export class FormsPackageApplicant {
  applicantId: number;
  applicantType: string;
  applicantName: string;
  benefitCategory: string;
  benefitCategoryId: string;
  easyVisaId: string;
}

export class PackageForm {
  formId: string;
  formName: string;
  displayText: string;
  signerName: string;
  hasCompleted: boolean;
  applicantId: number;
  questionConflictData: any;
}

export class PackageContinuationSheets {
  continuationSheetId: string;
  continuationSheetName: string;
  continuationSheetPage: string;
  continuationSheetPart: string;
  continuationSheetItem: string;
  formId: string;
  formName: string;
  hasCompleted: boolean;
  applicantId: number;
  questionConflictData: any;
}

export class FormsRequestModel {
  packageId: number;
  formInfoList: string | string[]; // its a combination of formId and applicantId, which is seperated by #
  continuationSheetInfoList: string | string[];  // its a combination of continuationSheetId and applicantId, which is seperated by #
}

export class BlankForm {
  formId: string;
  formName: string;
  displayText: string;
}


export enum FormContainsNativeAlphabet {
  Form_129F = 'Form_129F',
  Form_130 = 'Form_130'
}

export enum NativeAlphabetFormPages {
  Form_129F = 'Page 7 Part 2',
  Form_130 = 'Page 8 Part 4'
}
