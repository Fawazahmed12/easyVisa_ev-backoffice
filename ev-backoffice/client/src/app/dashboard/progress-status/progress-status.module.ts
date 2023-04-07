import { NgModule } from '@angular/core';

import { NgrxProgressStatusModule } from './ngrx/module';

import { SharedModule } from '../../shared/shared.module';
import { SelectPackageModule } from '../../components/select-package/select-package.module';
import { SpinnerModule } from '../../components/spinner/spinner.module';

import { ProgressStatusRoutingModule } from './progress-status-routing.module';
import { ProgressStatusComponent } from './progress-status.component';
import { ProgressStatusService } from './progress-status.service';
import { QuestionnaireProgressModule } from './questionnaire-progress/questionnaire-progress.module';
import { QuestionnaireStatusResolverService } from './resolvers/questionnaire-status-resolver.service';
import { DocumentPortalProgressModule } from './document-portal-progress/document-portal-progress.module';
import { DocumentStatusResolverService } from './resolvers/document-progress-resolver.service';
import { MyPackagesResolverService } from '../../core/resolvers/my-packages-resolver.service';

@NgModule({
  imports: [
    ProgressStatusRoutingModule,
    SharedModule,
    NgrxProgressStatusModule,
    QuestionnaireProgressModule,
    SelectPackageModule,
    DocumentPortalProgressModule,
    SpinnerModule
  ],
  declarations: [ProgressStatusComponent],
  providers: [
    MyPackagesResolverService,
    QuestionnaireStatusResolverService,
    DocumentStatusResolverService,
    ProgressStatusService,
  ],
})
export class ProgressStatusModule {
}
