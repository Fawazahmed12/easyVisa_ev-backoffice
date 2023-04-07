import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { ModalHeaderModule } from '../../../components/modal-header/modal-header.module';

import { SelectFormsContinuationSheetsModule } from '../select-forms-continuation-sheets/select-forms-continuation-sheets.module';

import { DownloadPrintComponent } from './download-print.component';



@NgModule({
  imports: [
    SharedModule,
    SelectFormsContinuationSheetsModule,
    ModalHeaderModule,
  ],
  declarations: [
    DownloadPrintComponent,
  ],
  exports: [
    DownloadPrintComponent
  ],
  entryComponents: [
    DownloadPrintComponent
  ]
})
export class DownloadPrintModule { }
