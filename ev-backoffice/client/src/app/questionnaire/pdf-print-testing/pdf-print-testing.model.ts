export class USCISFormModel {
  id: string;
  displayText: string;
  order: number;
  disabled: boolean;
  answered: boolean;
  continuationSheetList: ContinuationSheetModel[];
}


export class ContinuationSheetModel {
  id: string;
  order: number;
  displayText: string;
  sheetNumber: string;
  page: string;
  part: string;
  item: string;
}
