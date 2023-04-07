import { NgModule } from '@angular/core';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule } from '@angular/forms';

import { FormlyModule } from '@ngx-formly/core';

import { PdfViewerModule } from 'ng2-pdf-viewer';

import { SharedModule } from '../../../shared/shared.module';
import { SafeUrlPipeModule } from '../../../shared/pipes/safe-url/safeUrlPipe.module';
import { PdfPrintTestingComponent } from '../../pdf-print-testing/pdf-print-testing.component';

import { SectionComponent } from './section.component';
import { FormlyComponentsModule } from '../components/formly-components.module';
import { PdfViewerModalComponent } from '../../pdf-print-testing/pdf-viewer-modal/pdf-viewer-modal.component';
import { SectionFieldNavigationDirective } from './section.field.navigation.directive';
import { SpinnerModule } from '../../../components/spinner/spinner.module';

@NgModule({
  imports: [
    SharedModule,
    NgSelectModule,
    FormsModule,
    FormlyModule,
    FormlyComponentsModule,
    PdfViewerModule,
    SafeUrlPipeModule,
    SpinnerModule
  ],
  exports: [
    SectionComponent,
  ],
  declarations: [
    SectionComponent,
    PdfPrintTestingComponent,
    PdfViewerModalComponent,
    SectionFieldNavigationDirective
  ],
  entryComponents: [
    PdfViewerModalComponent
  ],
})
export class SectionModule {
}
