import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../../shared/shared.module';

import { DispositionCompleteModalComponent } from './disposition-complete-modal/disposition-complete-modal.component';
import { DispositionIncompleteModalComponent } from './disposition-incomplete-modal/disposition-incomplete-modal.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule
  ],
  declarations: [
    DispositionCompleteModalComponent,
    DispositionIncompleteModalComponent
  ],
  entryComponents: [
    DispositionCompleteModalComponent,
    DispositionIncompleteModalComponent
  ]
})
export class DocumentDispositionModule {
}
