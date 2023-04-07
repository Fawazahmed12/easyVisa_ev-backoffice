import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { ModalHeaderModule } from '../../../components/modal-header/modal-header.module';
import { SafeHtmlPipeModule } from '../../../shared/pipes/safe-html/safeHtmlPipe.module';

import { BenefitCategoryConflictModalComponent } from './benefit-category-conflict-modal.component';


@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
    SafeHtmlPipeModule
  ],
  declarations: [
    BenefitCategoryConflictModalComponent,
  ],
  exports: [
    BenefitCategoryConflictModalComponent,
  ],
  entryComponents: [
    BenefitCategoryConflictModalComponent,
  ]
})

export class BenefitCategoryConflictModalModule {
}
