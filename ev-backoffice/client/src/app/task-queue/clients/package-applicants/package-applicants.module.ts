import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';

import { PackageResolverService } from '../../resolvers/package-resolver.service';
import { ApplicantInfoModule } from '../../components/applicant-info/applicant-info.module';

import { PackageApplicantsRoutingModule } from './package-applicants-routing.module';
import { PackageApplicantsComponent } from './package-applicants.component';
import { EditAmountOwedModule } from './edit-amount-owed/edit-amount-owed.module';
import { SpinnerModule } from '../../../components/spinner/spinner.module';


@NgModule({
    imports: [
        SharedModule,
        ApplicantInfoModule,
        PackageApplicantsRoutingModule,
        EditAmountOwedModule,
        SpinnerModule,
    ],
  declarations: [
    PackageApplicantsComponent,
  ],
  providers: [
    PackageResolverService,
  ]

})
export class PackageApplicantsModule {
}
