import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { BlockModule } from '../../../shared/components/block/block.module';
import { ModalHeaderModule } from '../../../components/modal-header/modal-header.module';

import { AddNewNoteComponent } from './add-new-note.component';


@NgModule({
  imports: [
    SharedModule,
    BlockModule,
    ModalHeaderModule,
  ],
  declarations: [
    AddNewNoteComponent,
  ],
  exports: [
    AddNewNoteComponent,
  ],
  entryComponents: [
    AddNewNoteComponent,
  ]
})
export class AddNewNoteModule {
}
