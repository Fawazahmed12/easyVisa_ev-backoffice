import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../../shared/shared.module';
import { FileIconModule } from '../../components/file-icon/file-icon.module';
import { PreviewEmailModule } from '../../components/preview-email/preview-email.module';

import { ApplicantsListModule } from '../components/applicants-list/applicants-list.module';
import { ApplicantTypePipeModule } from '../pipes/applicantTypePipe.module';
import { EmailToPackageApplicantsComponent } from './email-to-package-applicants.component';
import { EmailToPackageApplicantsRoutingModule } from './email-to-package-applicants-routing.module';
import { TaxesInfoModule } from '../../components/taxes-info/taxes-info.module';


@NgModule({
    imports: [
        CommonModule,
        SharedModule,
        ApplicantTypePipeModule,
        ApplicantsListModule,
        EmailToPackageApplicantsRoutingModule,
        PreviewEmailModule,
        FileIconModule,
        TaxesInfoModule,
    ],
  declarations: [
    EmailToPackageApplicantsComponent,
  ],
})
export class EmailToPackageApplicantsModule { }
