import { NgModule } from '@angular/core';
import { DatePipe } from '@angular/common';

import { SharedModule } from '../../../../../shared/shared.module';
import { SpinnerModule } from '../../../../../components/spinner/spinner.module';
import { ModalHeaderModule } from '../../../../../components/modal-header/modal-header.module';
import { RepresentativeTypePipeModule } from '../../../../../shared/pipes/representative-type/representative-type-pipe.module';
import { TableModule } from '../../../../../components/table/table.module';
import { OrganizationTypeModule } from '../../../../../shared/pipes/organization-type/organization-type.module';
import { FindLabelPipe } from '../../../../../shared/pipes/find-label/find-label.pipe';

import { TransferCasesModalComponent } from './transfer-cases-modal.component';

@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
    TableModule,
    OrganizationTypeModule,
    RepresentativeTypePipeModule,
    SpinnerModule,
  ],
  declarations: [
    TransferCasesModalComponent,
  ],
  exports: [
    TransferCasesModalComponent,
  ],
  entryComponents: [
    TransferCasesModalComponent,
  ],
  providers: [
    FindLabelPipe,
    DatePipe
  ]
})

export class TransferCasesModalModule {
}
