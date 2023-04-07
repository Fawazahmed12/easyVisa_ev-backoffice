import { FormControl } from '@angular/forms';

export interface TableHeader {
  action?: boolean;
  title: string;
  colName: string;
  colClass?: string;
  sortBy?: boolean;
  sortColBy?: string;
  control?: FormControl;
  rowSpan?: number;
  colSpan?: number;
  bgMiddleBlue?: boolean;
  bgDarkBlueHeader?: boolean;
  textWrap?: boolean;
  smallHeader?: boolean;
  hideHeader?: boolean;
}
