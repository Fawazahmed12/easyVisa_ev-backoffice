import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../../../../shared/shared.module';
import { FindLabelPipeModule } from '../../../../shared/pipes/find-label/find-label-pipe.module';

import { GovernmentFeesModalComponent } from './government-fees-modal.component';


@NgModule({
  imports: [
    SharedModule,
    CommonModule,
    FindLabelPipeModule,
  ],
  declarations: [
    GovernmentFeesModalComponent,
  ],
  exports: [
    GovernmentFeesModalComponent,
  ]
})

export class GovernmentFeesModalModule {
}

