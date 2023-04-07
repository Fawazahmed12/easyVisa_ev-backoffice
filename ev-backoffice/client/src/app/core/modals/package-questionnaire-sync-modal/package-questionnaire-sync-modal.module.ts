import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PackageQuestionnaireSyncModalComponent } from './package-questionnaire-sync-modal.component';
import { SharedModule } from '../../../shared/shared.module';
import { ModalHeaderModule } from '../../../components/modal-header/modal-header.module';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    ModalHeaderModule,
  ],
  declarations: [
    PackageQuestionnaireSyncModalComponent
  ],
  exports: [
    PackageQuestionnaireSyncModalComponent
  ],
  entryComponents: [
    PackageQuestionnaireSyncModalComponent
  ]
})
export class PackageQuestionnaireSyncModalModule {
}
