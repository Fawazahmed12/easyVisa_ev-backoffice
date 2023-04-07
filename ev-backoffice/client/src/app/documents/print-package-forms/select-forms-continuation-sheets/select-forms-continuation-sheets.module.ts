import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { SelectFormsContinuationSheetsComponent } from './select-forms-continuation-sheets.component';


@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [
    SelectFormsContinuationSheetsComponent,
  ],
  exports: [
    SelectFormsContinuationSheetsComponent
  ],
  entryComponents: [
    SelectFormsContinuationSheetsComponent
  ]
})
export class SelectFormsContinuationSheetsModule { }
