import { NgModule } from '@angular/core';

import { NgbAccordionModule } from '@ng-bootstrap/ng-bootstrap';

import { SharedModule } from '../../shared/shared.module';

import { PrintPackageFormsComponent } from './print-package-forms.component';
import { SelectApplicantModule } from './select-applicant/select-applicant.module';
import { SelectFormsContinuationSheetsModule } from './select-forms-continuation-sheets/select-forms-continuation-sheets.module';
import { DownloadPrintModule } from './download-print/download-print.module';
import { RepresentativeTypePipeModule } from '../../shared/pipes/representative-type/representative-type-pipe.module';


@NgModule({
  imports: [
    SharedModule,
    NgbAccordionModule,
    SelectApplicantModule,
    SelectFormsContinuationSheetsModule,
    DownloadPrintModule,
    RepresentativeTypePipeModule
  ],
  declarations: [
    PrintPackageFormsComponent,
  ],
  exports: [
    PrintPackageFormsComponent,
  ]
})
export class PrintPackageFormsModule { }
